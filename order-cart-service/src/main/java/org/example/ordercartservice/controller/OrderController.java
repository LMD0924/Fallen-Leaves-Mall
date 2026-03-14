package org.example.ordercartservice.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.common.RestBean;
import org.example.ordercartservice.dto.CreateOrderDTO;
import org.example.ordercartservice.vo.OrderDetailVO;
import org.example.ordercartservice.dto.PayOrderDTO;
import org.example.ordercartservice.service.OrderService;
import org.example.ordercartservice.vo.OrderListVO;
import org.springframework.web.bind.annotation.*;


@Tag(name = "订单模块")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

//    @ApiOperation("创建订单（从购物车）")
    @PostMapping("/create")
    public RestBean<OrderDetailVO> createOrder(@Valid @RequestBody CreateOrderDTO dto) {
        dto.setFromCart(true);
        OrderDetailVO order = orderService.createOrder(dto);
        return RestBean.success(order);
    }

//    @ApiOperation("立即购买")
    @PostMapping("/buyNow")
    public RestBean<OrderDetailVO> buyNow(@Valid @RequestBody CreateOrderDTO dto) {
        dto.setFromCart(false);
        OrderDetailVO order = orderService.buyNow(dto);
        return RestBean.success(order);
    }

 //   @ApiOperation("支付订单")
    @PostMapping("/pay")
    public RestBean<OrderDetailVO> payOrder(@Valid @RequestBody PayOrderDTO dto) {
        OrderDetailVO order = orderService.payOrder(dto);
        return RestBean.success(order);
    }

 //   @ApiOperation("取消订单")
    @PostMapping("/cancel/{orderId}")
    public RestBean<Void> cancelOrder(@PathVariable Long orderId,
                                          @RequestParam Long userId) {
        orderService.cancelOrder(orderId, userId);
        return RestBean.success(null);
    }

  //  @ApiOperation("确认收货")
    @PostMapping("/confirm/{orderId}")
    public RestBean<Void> confirmReceive(@PathVariable Long orderId,
                                             @RequestParam Long userId) {
        orderService.confirmReceive(orderId, userId);
        return RestBean.success(null);
    }

 //  @ApiOperation("订单详情")
    @GetMapping("/detail/{orderId}")
    public RestBean<OrderDetailVO> getOrderDetail(@PathVariable Long orderId,
                                                      @RequestParam Long userId) {
        OrderDetailVO order = orderService.getOrderDetail(orderId, userId);
        return RestBean.success(order);
    }

  //  @ApiOperation("订单列表")
    @GetMapping("/list")
    public RestBean<Page<OrderListVO>> getOrderList(@RequestParam Long userId,
                                                    @RequestParam(required = false) Integer status,
                                                    @RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<OrderListVO> page = orderService.getOrderList(userId, status, pageNum, pageSize);
        return RestBean.success(page);
    }
}