package org.example.seckillsession.client;

import org.example.backend.common.RestBean;
import org.example.ordercartservice.dto.CreateOrderDTO;
import org.example.ordercartservice.vo.OrderDetailVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/*
 * @Author:总会落叶
 * @Date:2026/3/13
 * @Description:
 */
@FeignClient(name = "order-service", path = "/api/order")
public interface OrderFeignClient {

    @PostMapping("/seckill/create")
    RestBean<OrderDetailVO> createSeckillOrder(@RequestBody CreateOrderDTO dto);
}
