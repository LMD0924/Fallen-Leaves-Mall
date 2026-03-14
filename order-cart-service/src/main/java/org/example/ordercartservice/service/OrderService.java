package org.example.ordercartservice.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.ordercartservice.dto.CreateOrderDTO;
import org.example.ordercartservice.vo.OrderDetailVO;
import org.example.ordercartservice.dto.PayOrderDTO;
import org.example.ordercartservice.vo.OrderListVO;

public interface OrderService {

    /**
     * 创建订单（从购物车）
     */
    OrderDetailVO createOrder(CreateOrderDTO dto);

    /**
     * 直接购买（立即购买）
     */
    OrderDetailVO buyNow(CreateOrderDTO dto);

    /**
     * 支付订单
     */
    OrderDetailVO payOrder(PayOrderDTO dto);

    /**
     * 取消订单
     */
    void cancelOrder(Long orderId, Long userId);

    /**
     * 确认收货
     */
    void confirmReceive(Long orderId, Long userId);

    /**
     * 查询订单详情
     */
    OrderDetailVO getOrderDetail(Long orderId, Long userId);

    /**
     * 查询订单列表
     */
    Page<OrderListVO> getOrderList(Long userId, Integer status, Integer pageNum, Integer pageSize);
}