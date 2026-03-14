package org.example.ordercartservice.common;

import org.example.ordercartservice.entity.Order;
import org.example.ordercartservice.mapper.OrderMapper;
import org.example.ordercartservice.service.Impl.OrderServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/*
 * @Author:总会落叶
 * @Date:2026/3/13
 * @Description:
 */
@Component
public class OrderTimeoutHandler {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 发送延迟消息（订单创建时调用）
     */
    public void sendTimeoutMessage(Long orderId) {
        // 使用RabbitMQ延迟插件
        rabbitTemplate.convertAndSend("order.delayed.exchange",
                "order.timeout", orderId, message -> {
                    message.getMessageProperties().setDelayLong((long) (30 * 60 * 1000)); // 30分钟
                    return message;
                });
    }

    /**
     * 消费延迟消息（超时取消订单）
     */
    @RabbitListener(queues = "order.timeout.queue")
    public void handleTimeout(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order != null && order.getStatus() == OrderServiceImpl.STATUS_PENDING_PAY) {
            order.setStatus(OrderServiceImpl.STATUS_CANCELLED);
            order.setCancelTime(LocalDateTime.now());
            orderMapper.updateById(order);

            // 恢复库存
            // productFeignClient.restoreStock(order.getOrderNo());
        }
    }
}
