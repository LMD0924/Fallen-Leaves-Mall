package org.example.seckillsession.service.Impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.common.RestBean;
import org.example.ordercartservice.client.ProductFeignClient;
import org.example.ordercartservice.dto.CreateOrderDTO;
import org.example.ordercartservice.dto.OrderItemDTO;
import org.example.ordercartservice.vo.OrderDetailVO;
import org.example.ordercartservice.vo.ProductSkuVO;
import org.example.seckillsession.client.OrderFeignClient;
import org.example.seckillsession.dto.SeckillRequestDTO;
import org.example.seckillsession.entity.SeckillOrder;
import org.example.seckillsession.entity.SeckillProduct;
import org.example.seckillsession.entity.SeckillSession;
import org.example.seckillsession.mapper.SeckillOrderMapper;
import org.example.seckillsession.mapper.SeckillProductMapper;
import org.example.seckillsession.mapper.SeckillSessionMapper;
import org.example.seckillsession.service.SeckillService;
import org.example.seckillsession.vo.SeckillProductVO;
import org.example.seckillsession.vo.SeckillResultVO;
import org.example.seckillsession.vo.SeckillSessionVO;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeckillServiceImpl implements SeckillService {

    private final SeckillProductMapper seckillProductMapper;
    private final SeckillSessionMapper sessionMapper;
    private final SeckillOrderMapper seckillOrderMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductFeignClient productFeignClient;
    private final OrderFeignClient orderFeignClient;
    private final HttpServletRequest request;

    // Redis key前缀
    private static final String SECKILL_STOCK_KEY = "seckill:stock:";
    private static final String SECKILL_SOLD_KEY = "seckill:sold:";
    private static final String SECKILL_USER_KEY = "seckill:user:";
    private static final String SECKILL_RESULT_KEY = "seckill:result:";
    private static final String SECKILL_TOKEN_KEY = "seckill:token:";
    private static final String SECKILL_SESSION_KEY = "seckill:session:";
    private static final String SECKILL_PRODUCT_KEY = "seckill:product:";

    // Lua脚本：秒杀库存扣减
    private static final String SECKILL_SCRIPT =
            "local stock_key = KEYS[1] " +  // 库存key
                    "local sold_key = KEYS[2] " +   // 已售key
                    "local user_key = KEYS[3] " +   // 用户标记key
                    "local stock = redis.call('get', stock_key) " +
                    "if not stock or tonumber(stock) <= 0 then " +
                    "    return 0 " +  // 库存不足
                    "end " +
                    "local user_exists = redis.call('sismember', user_key, ARGV[2]) " +
                    "if user_exists == 1 then " +
                    "    return 2 " +  // 重复秒杀
                    "end " +
                    "redis.call('decr', stock_key) " +
                    "redis.call('incr', sold_key) " +
                    "redis.call('sadd', user_key, ARGV[2]) " +
                    "redis.call('expire', user_key, 86400) " +  // 24小时过期
                    "return 1";  // 成功

    @Override
    public List<SeckillSessionVO> getSessions() {
        // 优先从缓存获取
        String cacheKey = "seckill:sessions";
        List<SeckillSessionVO> cacheList = (List<SeckillSessionVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cacheList != null) {
            return cacheList;
        }

        // 查询所有场次
        LambdaQueryWrapper<SeckillSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SeckillSession::getStartTime);
        List<SeckillSession> sessions = sessionMapper.selectList(wrapper);

        // 转换VO
        List<SeckillSessionVO> result = sessions.stream().map(session -> {
            SeckillSessionVO vo = new SeckillSessionVO();
            BeanUtils.copyProperties(session, vo);

            // 设置状态
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(session.getStartTime())) {
                vo.setStatus(0); // 未开始
                vo.setStatusText("即将开始");
            } else if (now.isAfter(session.getEndTime())) {
                vo.setStatus(2); // 已结束
                vo.setStatusText("已结束");
            } else {
                vo.setStatus(1); // 进行中
                vo.setStatusText("秒杀中");
            }

            return vo;
        }).collect(Collectors.toList());

        // 缓存5分钟
        redisTemplate.opsForValue().set(cacheKey, result, 5, TimeUnit.MINUTES);

        return result;
    }

    @Override
    public List<SeckillProductVO> getProductsBySession(Long sessionId) {
        String cacheKey = "seckill:session:products:" + sessionId;
        List<SeckillProductVO> cacheList = (List<SeckillProductVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cacheList != null) {
            return cacheList;
        }

        // 查询秒杀商品
        List<SeckillProduct> products = seckillProductMapper.selectBySession(sessionId);

        List<SeckillProductVO> result = products.stream().map(sp -> {
            SeckillProductVO vo = new SeckillProductVO();
            BeanUtils.copyProperties(sp, vo);

            // 从Redis获取实时库存
            String stockKey = SECKILL_STOCK_KEY + sp.getId();
            Integer stock = (Integer) redisTemplate.opsForValue().get(stockKey);
            vo.setSeckillStock(stock != null ? stock : sp.getSeckillStock());

            // 查询商品详情
            RestBean<ProductSkuVO> restBean = productFeignClient.getSkuInfo(sp.getSkuId());
            ProductSkuVO sku= restBean.getData();
            if (sku != null) {
                vo.setProductName(sku.getProductName());
                vo.setProductImage(sku.getImage());
                vo.setOriginalPrice(sku.getPrice());
            }

            return vo;
        }).collect(Collectors.toList());

        // 缓存1分钟
        redisTemplate.opsForValue().set(cacheKey, result, 1, TimeUnit.MINUTES);

        return result;
    }

    @Override
    public SeckillProductVO getProductDetail(Long seckillId) {
        String cacheKey = SECKILL_PRODUCT_KEY + seckillId;
        SeckillProductVO cache = (SeckillProductVO) redisTemplate.opsForValue().get(cacheKey);
        if (cache != null) {
            return cache;
        }

        SeckillProduct sp = seckillProductMapper.selectById(seckillId);
        if (sp == null) {
            throw new RuntimeException("秒杀商品不存在");
        }

        SeckillProductVO vo = new SeckillProductVO();
        BeanUtils.copyProperties(sp, vo);

        // Redis实时库存
        String stockKey = SECKILL_STOCK_KEY + seckillId;
        Integer stock = (Integer) redisTemplate.opsForValue().get(stockKey);
        vo.setSeckillStock(stock != null ? stock : sp.getSeckillStock());

        // 商品详情
        RestBean<ProductSkuVO> restBean = productFeignClient.getSkuInfo(sp.getSkuId());
        ProductSkuVO sku = restBean.getData();
        if (sku != null) {
            vo.setProductName(sku.getProductName());
            vo.setProductImage(sku.getImage());
            vo.setOriginalPrice(sku.getPrice());
            vo.setProductDetail(sku.getDetail());
        }

        // 场次信息
        SeckillSession session = sessionMapper.selectById(sp.getSessionId());
        if (session != null) {
            vo.setSessionName(session.getName());
            vo.setStartTime(session.getStartTime());
            vo.setEndTime(session.getEndTime());
        }

        redisTemplate.opsForValue().set(cacheKey, vo, 30, TimeUnit.SECONDS);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeckillResultVO seckill(SeckillRequestDTO dto) {
        Long userId = dto.getUserId();
        Long seckillId = dto.getSeckillId();
        Integer quantity = dto.getQuantity() != null ? dto.getQuantity() : 1;

        // 1. 前置限流（令牌桶）
        if (!tryAcquire(userId)) {
            log.warn("用户{}被限流", userId);
            saveLog(seckillId, userId, 4); // 限流
            return SeckillResultVO.fail("请求太频繁，请稍后重试");
        }

        // 2. 获取秒杀商品信息
        SeckillProduct sp = seckillProductMapper.selectById(seckillId);
        if (sp == null) {
            throw new RuntimeException("秒杀商品不存在");
        }

        // 3. 校验场次时间
        SeckillSession session = sessionMapper.selectById(sp.getSessionId());
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(session.getStartTime())) {
            return SeckillResultVO.fail("秒杀未开始");
        }
        if (now.isAfter(session.getEndTime())) {
            return SeckillResultVO.fail("秒杀已结束");
        }

        // 4. 校验限购
        if (quantity > sp.getSeckillLimit()) {
            return SeckillResultVO.fail("超过限购数量");
        }

        // 5. 原子扣减库存（Lua脚本）
        String stockKey = SECKILL_STOCK_KEY + seckillId;
        String soldKey = SECKILL_SOLD_KEY + seckillId;
        String userKey = SECKILL_USER_KEY + seckillId;

        DefaultRedisScript<Long> script = new DefaultRedisScript<>(SECKILL_SCRIPT, Long.class);
        Long result = redisTemplate.execute(script,
                Arrays.asList(stockKey, soldKey, userKey),
                quantity.toString(), userId.toString());

        if (result == null || result == 0) {
            log.info("秒杀失败：库存不足，seckillId={}, userId={}", seckillId, userId);
            saveLog(seckillId, userId, 2); // 库存不足
            return SeckillResultVO.fail("手慢了，商品已抢完");
        }

        if (result == 2) {
            log.info("秒杀失败：重复秒杀，seckillId={}, userId={}", seckillId, userId);
            saveLog(seckillId, userId, 3); // 重复秒杀
            return SeckillResultVO.fail("您已经参与过该秒杀");
        }

        // 6. 生成预下单记录
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setSeckillId(seckillId);
        seckillOrder.setSessionId(sp.getSessionId());
        seckillOrder.setProductId(sp.getProductId());
        seckillOrder.setSkuId(sp.getSkuId());
        seckillOrder.setUserId(userId);
        seckillOrder.setSeckillPrice(sp.getSeckillPrice());
        seckillOrder.setQuantity(quantity);
        seckillOrder.setStatus(0); // 预下单
        seckillOrderMapper.insert(seckillOrder);

        // 7. 生成唯一令牌（用于后续查询）
        String token = IdUtil.fastSimpleUUID();
        String tokenKey = SECKILL_TOKEN_KEY + userId + ":" + seckillId;
        redisTemplate.opsForValue().set(tokenKey, token, 5, TimeUnit.MINUTES);

        // 8. 异步创建正式订单
        createOrderAsync(seckillOrder);

        // 9. 记录成功日志
        saveLog(seckillId, userId, 1);

        // 10. 返回结果
        SeckillResultVO vo = SeckillResultVO.success();
        vo.setSeckillId(seckillId);
        vo.setToken(token);
        vo.setMessage("抢购成功，正在为您创建订单");

        return vo;
    }

    @Override
    public SeckillResultVO getResult(Long userId, Long seckillId) {
        // 查询用户秒杀记录
        LambdaQueryWrapper<SeckillOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeckillOrder::getUserId, userId)
                .eq(SeckillOrder::getSeckillId, seckillId);
        SeckillOrder order = seckillOrderMapper.selectOne(wrapper);

        if (order == null) {
            return SeckillResultVO.fail("未参与该秒杀");
        }

        SeckillResultVO vo = new SeckillResultVO();
        vo.setSeckillId(seckillId);

        switch (order.getStatus()) {
            case 0:
                vo.setSuccess(true);
                vo.setMessage("抢购成功，订单创建中");
                vo.setOrderId(order.getOrderId());
                vo.setOrderNo(order.getOrderNo());
                break;
            case 1:
                vo.setSuccess(true);
                vo.setMessage("支付成功");
                vo.setOrderId(order.getOrderId());
                vo.setOrderNo(order.getOrderNo());
                break;
            case 2:
                vo.setSuccess(false);
                vo.setMessage("已取消");
                break;
            case 3:
                vo.setSuccess(false);
                vo.setMessage("订单已过期");
                break;
        }

        return vo;
    }

    @Override
    public void preheatSeckillStock(Long seckillId) {
        SeckillProduct sp = seckillProductMapper.selectById(seckillId);
        if (sp != null) {
            String stockKey = SECKILL_STOCK_KEY + seckillId;
            String soldKey = SECKILL_SOLD_KEY + seckillId;

            redisTemplate.opsForValue().set(stockKey, sp.getSeckillStock());
            redisTemplate.opsForValue().set(soldKey, 0);

            // 清除用户集合（防止脏数据）
            String userKey = SECKILL_USER_KEY + seckillId;
            redisTemplate.delete(userKey);

            log.info("秒杀库存预热完成: seckillId={}, stock={}", seckillId, sp.getSeckillStock());
        }
    }

    @Override
    @Scheduled(cron = "0/5 * * * * ?") // 每5秒执行一次
    public void updateSessionStatus() {
        log.debug("开始更新秒杀场次状态");

        // 更新进行中的场次
        List<SeckillSession> currentSessions = sessionMapper.selectCurrentSessions();
        for (SeckillSession session : currentSessions) {
            if (session.getStatus() != 1) {
                sessionMapper.updateStatus(session.getId(), 1);
                log.info("场次已开始: {}", session.getName());

                // 预热该场次所有商品
                List<SeckillProduct> products = seckillProductMapper.selectBySession(session.getId());
                for (SeckillProduct sp : products) {
                    preheatSeckillStock(sp.getId());
                }
            }
        }

        // 更新已结束的场次
        LambdaQueryWrapper<SeckillSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeckillSession::getStatus, 1)
                .le(SeckillSession::getEndTime, LocalDateTime.now());
        List<SeckillSession> endedSessions = sessionMapper.selectList(wrapper);
        for (SeckillSession session : endedSessions) {
            sessionMapper.updateStatus(session.getId(), 2);
            log.info("场次已结束: {}", session.getName());
        }
    }

    /**
     * 定时任务：取消超时未支付订单
     */
    @Scheduled(cron = "0/30 * * * * ?") // 每30秒执行一次
    @Transactional(rollbackFor = Exception.class)
    public void cancelExpiredOrders() {
        int count = seckillOrderMapper.cancelExpiredOrders();
        if (count > 0) {
            log.info("取消超时秒杀订单: {}", count);

            // TODO: 恢复库存
            // 需要查询被取消的订单，恢复Redis库存
        }
    }

    /**
     * 初始化：预热当前进行中的秒杀
     */
    @PostConstruct
    public void initCurrentSeckill() {
        List<SeckillProduct> products = seckillProductMapper.selectCurrentSeckill();
        for (SeckillProduct sp : products) {
            preheatSeckillStock(sp.getId());
        }
        log.info("当前秒杀商品预热完成，数量：{}", products.size());
    }

    /**
     * 令牌桶限流
     */
    private boolean tryAcquire(Long userId) {
        // 简单限流：每个用户每秒最多10次请求
        String key = "seckill:rate:user:" + userId;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            redisTemplate.expire(key, 1, TimeUnit.SECONDS);
        }
        return count <= 10;
    }

    /**
     * 异步创建正式订单
     */
    private void createOrderAsync(SeckillOrder seckillOrder) {
        // TODO: 发送到MQ
        new Thread(() -> {
            try {
                // 模拟处理延迟
                Thread.sleep(100);

                // 调用订单服务创建订单
                CreateOrderDTO orderDTO = new CreateOrderDTO();
                orderDTO.setUserId(seckillOrder.getUserId());
                orderDTO.setFromCart(false);
                orderDTO.setReceiverName("秒杀用户"); // 实际应从用户信息获取
                orderDTO.setReceiverPhone("13800138000");
                orderDTO.setReceiverAddress("默认地址");

                OrderItemDTO item = new OrderItemDTO();
                item.setSkuId(seckillOrder.getSkuId());
                item.setCount(seckillOrder.getQuantity());
                orderDTO.setItems(Arrays.asList(item));

                RestBean<OrderDetailVO> restBean = orderFeignClient.createSeckillOrder(orderDTO);
                OrderDetailVO order = restBean.getData();
                // 更新秒杀订单
                seckillOrder.setOrderId(order.getId());
                seckillOrder.setOrderNo(order.getOrderNo());
                seckillOrderMapper.updateById(seckillOrder);

                log.info("秒杀订单创建成功: seckillOrderId={}, orderNo={}",
                        seckillOrder.getId(), order.getOrderNo());

            } catch (Exception e) {
                log.error("创建秒杀订单失败", e);
                // TODO: 补偿机制
            }
        }).start();
    }

    /**
     * 保存日志
     */
    private void saveLog(Long seckillId, Long userId, Integer status) {
        // 异步保存
        // TODO: 发送到MQ
    }
}