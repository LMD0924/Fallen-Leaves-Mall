package org.example.ordercartservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.common.RestBean;
import org.example.ordercartservice.dto.AddToCartDTO;
import org.example.ordercartservice.dto.UpdateCartDTO;
import org.example.ordercartservice.service.CartService;
import org.example.ordercartservice.vo.CartVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "购物车模块")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "添加商品到购物车")
    @PostMapping("/add")
    public RestBean<CartVO> addToCart(@Valid @RequestBody AddToCartDTO dto) {
        CartVO cartVO = cartService.addToCart(dto);
        return RestBean.success(cartVO);
    }

 //   @ApiOperation("更新购物车商品数量")
    @PostMapping("/update")
    public RestBean<CartVO> updateCart(@Valid @RequestBody UpdateCartDTO dto) {
        CartVO cartVO = cartService.updateCart(dto);
        return RestBean.success(cartVO);
    }

//    @ApiOperation("删除购物车商品")
    @DeleteMapping("/remove")
    public RestBean<Void> removeFromCart(@RequestParam Long userId,
                                             @RequestParam List<Long> cartIds) {
        cartService.removeFromCart(userId, cartIds);
        return RestBean.success(null);
    }

//    @ApiOperation("获取购物车列表")
    @GetMapping("/list")
    public RestBean<CartVO> getCartList(@RequestParam Long userId) {
        CartVO cartVO = cartService.getCartList(userId);
        return RestBean.success(cartVO);
    }

//    @ApiOperation("选中/取消选中")
    @PostMapping("/select")
    public RestBean<CartVO> selectCart(@RequestParam Long userId,
                                           @RequestParam List<Long> cartIds,
                                           @RequestParam Boolean selected) {
        CartVO cartVO = cartService.selectCart(userId, cartIds, selected);
        return RestBean.success(cartVO);
    }

 //   @ApiOperation("全选/全不选")
    @PostMapping("/selectAll")
    public RestBean<CartVO> selectAll(@RequestParam Long userId,
                                          @RequestParam Boolean selected) {
        CartVO cartVO = cartService.selectAll(userId, selected);
        return RestBean.success(cartVO);
    }
}