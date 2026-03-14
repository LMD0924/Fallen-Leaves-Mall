package org.example.ordercartservice.service.Impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.fallenleaves.order.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ordercartservice.client.ProductFeignClient;
import org.example.ordercartservice.dto.*;
import org.example.ordercartservice.entity.Order;
import org.example.ordercartservice.mapper.CartMapper;
import org.example.ordercartservice.mapper.OrderItemMapper;
import org.example.ordercartservice.mapper.OrderMapper;
import org.example.ordercartservice.service.OrderService;
import org.example.ordercartservice.vo.OrderDetailVO;
import org.example.ordercartservice.vo.OrderListVO;
import org.example.ordercartservice.vo.ProductSkuVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
 //   private final OrderLogMapper orderLogMapper;
    private final CartMapper cartMapper;
    private final ProductFeignClient productFeignClient;
    private final RedissonClient redissonClient;

    // 订单状态常量
    public static final int STATUS_PENDING_PAY = 0;
    public static final int STATUS_PAID = 1;
    public static final int STATUS_DELIVERED = 2;
    public static final int STATUS_COMPLETED = 3;
    public static final int STATUS_CANCELLED = 4;
    public static final int STATUS_REFUNDING = 5;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDetailVO createOrder(CreateOrderDTO dto) {
        // 1. 幂等性校验（防止重复提交）
        String lockKey = "order:create:" + dto.getUserId() + ":" + System.currentTimeMillis() / 1000;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                throw new RuntimeException("操作太频繁，请稍后重试");
            }

            // 2. 从购物车获取商品
            List<OrderItemDTO> items = dto.getItems();
            if (items == null || items.isEmpty()) {
                throw new RuntimeException("请选择要购买的商品");
            }

            // 3. 校验库存并计算金额
            List<Long> skuIds = items.stream()
                    .map(OrderItemDTO::getSkuId)
                    .collect(Collectors.toList());
            Map<Long, ProductSkuVO> skuMap = productFeignClient.batchGetSkuInfo(skuIds);

            BigDecimal totalAmount = BigDecimal.ZERO;
            List<OrderItem> orderItems = new ArrayList<>();

            for (OrderItemDTO item : items) {
                ProductSkuVO sku = skuMap.get(item.getSkuId());
                if (sku == null) {
                    throw new RuntimeException("商品不存在：" + item.getSkuId());
                }
                if (sku.getStock() < item.getCount()) {
                    throw new RuntimeException("商品库存不足：" + sku.getProductName());
                }

                // 计算金额
                BigDecimal itemAmount = sku.getPrice().multiply(new BigDecimal(item.getCount()));
                totalAmount = totalAmount.add(itemAmount);

                // 构建订单明细
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(sku.getProductId());
                orderItem.setSkuId(item.getSkuId());
                orderItem.setProductName(sku.getProductName());
                orderItem.setProductImage(sku.getImage());
                orderItem.setSkuSpecs(sku.getSpecs());
                orderItem.setPrice(sku.getPrice());
                orderItem.setCount(item.getCount());
                orderItem.setTotalAmount(itemAmount);
                orderItems.add(orderItem);
            }

            // 4. 生成订单号
            String orderNo = generateOrderNo(dto.getUserId());

            // 5. 创建订单
            Order order = new Order();
            order.setOrderNo(orderNo);
            order.setUserId(dto.getUserId());
            order.setTotalAmount(totalAmount);
            order.setPayAmount(totalAmount); // 暂不考虑优惠
            order.setFreightAmount(BigDecimal.ZERO);
            order.setDiscountAmount(BigDecimal.ZERO);
            order.setStatus(STATUS_PENDING_PAY);
            order.setReceiverName(dto.getReceiverName());
            order.setReceiverPhone(dto.getReceiverPhone());
            order.setReceiverAddress(dto.getReceiverAddress());
            order.setRemark(dto.getRemark());
            orderMapper.insert(order);

            // 6. 保存订单明细
            for (OrderItem item : orderItems) {
                item.setOrderId(order.getId());
                item.setOrderNo(orderNo);
                orderItemMapper.insert(item);
            }

            // 7. 扣减库存（调用商品服务）
            DeductStockDTO deductDTO = new DeductStockDTO();
            deductDTO.setOrderNo(orderNo);
           // deductDTO.setItems(items);
            boolean deductResult = productFeignClient.deductStock(deductDTO);
            if (!deductResult) {
                throw new RuntimeException("扣减库存失败");
            }

            // 8. 记录日志
/*            OrderLog log = new OrderLog();
            log.setOrderId(order.getId());
            log.setOrderNo(orderNo);
            log.setAction("CREATE");
            log.setFromStatus(null);
            log.setToStatus(STATUS_PENDING_PAY);
            log.setRemark("创建订单");
            orderLogMapper.insert(log);*/

            // 9. 删除购物车中已下单的商品
            if (dto.getFromCart()) {
                cartMapper.clearSelected(dto.getUserId());
            }

            return getOrderDetail(order.getId(), dto.getUserId());

        } catch (InterruptedException e) {
            throw new RuntimeException("系统繁忙，请稍后重试");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public OrderDetailVO buyNow(CreateOrderDTO dto) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDetailVO payOrder(PayOrderDTO dto) {
        // 1. 查询订单
        Order order = orderMapper.selectById(dto.getOrderId());
        if (order == null || !order.getUserId().equals(dto.getUserId())) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 校验状态
        if (order.getStatus() != STATUS_PENDING_PAY) {
            throw new RuntimeException("订单状态错误");
        }

        // 3. 调用支付服务（模拟）
        // TODO: 调用微信/支付宝支付接口

        // 4. 更新订单状态
        int updated = orderMapper.updateStatus(order.getId(), STATUS_PENDING_PAY, STATUS_PAID);
        if (updated == 0) {
            throw new RuntimeException("支付失败");
        }

        // 5. 更新订单支付信息
        order.setPayType(dto.getPayType());
        order.setPaymentTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 6. 记录日志
/*        OrderLog log = new OrderLog();
        log.setOrderId(order.getId());
        log.setOrderNo(order.getOrderNo());
        log.setAction("PAY");
        log.setFromStatus(STATUS_PENDING_PAY);
        log.setToStatus(STATUS_PAID);
        log.setRemark("支付成功");
        orderLogMapper.insert(log);*/

        return getOrderDetail(order.getId(), dto.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }

        // 只有待付款才能取消
        if (order.getStatus() != STATUS_PENDING_PAY) {
            throw new RuntimeException("该状态不能取消");
        }

        int updated = orderMapper.updateStatus(orderId, STATUS_PENDING_PAY, STATUS_CANCELLED);
        if (updated > 0) {
            order.setCancelTime(LocalDateTime.now());
            orderMapper.updateById(order);

            // 恢复库存（调用商品服务）
            productFeignClient.restoreStock(order.getOrderNo());

            // 记录日志
/*            OrderLog log = new OrderLog();
            log.setOrderId(order.getId());
            log.setOrderNo(order.getOrderNo());
            log.setAction("CANCEL");
            log.setFromStatus(STATUS_PENDING_PAY);
            log.setToStatus(STATUS_CANCELLED);
            log.setRemark("用户取消");
            orderLogMapper.insert(log);*/
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }

        if (order.getStatus() != STATUS_DELIVERED) {
            throw new RuntimeException("订单状态错误");
        }

        int updated = orderMapper.updateStatus(orderId, STATUS_DELIVERED, STATUS_COMPLETED);
        if (updated > 0) {
            order.setReceiveTime(LocalDateTime.now());
            orderMapper.updateById(order);

/*            OrderLog log = new OrderLog();
            log.setOrderId(order.getId());
            log.setOrderNo(order.getOrderNo());
            log.setAction("RECEIVE");
            log.setFromStatus(STATUS_DELIVERED);
            log.setToStatus(STATUS_COMPLETED);
            orderLogMapper.insert(log);*/
        }
    }

    @Override
    public OrderDetailVO getOrderDetail(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }

        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);

        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setStatus(order.getStatus());
        vo.setStatusText(getStatusText(order.getStatus()));
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setCreateTime(order.getCreateTime());
        vo.setPaymentTime(order.getPaymentTime());
        vo.setDeliveryTime(order.getDeliveryTime());
        vo.setReceiveTime(order.getReceiveTime());

        List<OrderItemVO> itemVOs = items.stream().map(item -> {
            OrderItemVO itemVO = new OrderItemVO();
            itemVO.setProductId(item.getProductId());
            itemVO.setSkuId(item.getSkuId());
            itemVO.setProductName(item.getProductName());
            itemVO.setProductImage(item.getProductImage());
            itemVO.setPrice(item.getPrice());
            itemVO.setCount(item.getCount());
            itemVO.setTotalAmount(item.getTotalAmount());
            return itemVO;
        }).collect(Collectors.toList());
        vo.setItems(itemVOs);

        return vo;
    }

    @Override
    public Page<OrderListVO> getOrderList(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);

        Page<Order> page = new Page<>(pageNum, pageSize);
        page(page, wrapper);

        Page<OrderListVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<OrderListVO> list = page.getRecords().stream().map(order -> {
            OrderListVO vo = new OrderListVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setPayAmount(order.getPayAmount());
            vo.setStatus(order.getStatus());
            vo.setStatusText(getStatusText(order.getStatus()));
            vo.setCreateTime(order.getCreateTime());

            // 查询订单商品（取第一个作为预览图）
/*            List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
            if (!items.isEmpty()) {
                vo.setProductImage(items.get(0).getProductImage());
                vo.setProductName(items.get(0).getProductName());
                vo.setProductCount(items.size());
            }*/

            return vo;
        }).collect(Collectors.toList());
        result.setRecords(list);

        return result;
    }

    /**
     * 生成订单号
     * 格式：年月日 + 用户ID后6位 + 随机数
     */
    private String generateOrderNo(Long userId) {
        String dateStr = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String userIdStr = String.format("%06d", userId % 1000000);
        String randomStr = IdUtil.fastSimpleUUID().substring(0, 6);
        return dateStr + userIdStr + randomStr;
    }

    /**
     * 获取状态文字
     */
    private String getStatusText(Integer status) {
        switch (status) {
            case STATUS_PENDING_PAY: return "待付款";
            case STATUS_PAID: return "已付款";
            case STATUS_DELIVERED: return "已发货";
            case STATUS_COMPLETED: return "已完成";
            case STATUS_CANCELLED: return "已取消";
            case STATUS_REFUNDING: return "售后中";
            default: return "未知";
        }
    }
}