package org.example.couponservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.common.RestBean;
import org.example.couponservice.dto.ReceiveCouponDTO;
import org.example.couponservice.dto.UseCouponDTO;
import org.example.couponservice.service.CouponService;
import org.example.couponservice.vo.CouponVO;
import org.example.couponservice.vo.UserCouponVO;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@Tag(name = "优惠券模块")
@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "领取优惠券")
    @PostMapping("/receive")
    public RestBean<UserCouponVO> receiveCoupon(@Valid @RequestBody ReceiveCouponDTO dto) {
        UserCouponVO userCoupon = couponService.receiveCoupon(dto);
        return RestBean.success(userCoupon);
    }

    @Operation(summary = "可领取优惠券列表")
    @GetMapping("/available")
    public RestBean<Page<CouponVO>> getAvailableCoupons(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<CouponVO> page = couponService.getAvailableCoupons(pageNum, pageSize);
        return RestBean.success(page);
    }

    @Operation(summary = "我的优惠券列表")
    @GetMapping("/my")
    public RestBean<Page<UserCouponVO>> getUserCoupons(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<UserCouponVO> page = couponService.getUserCoupons(userId, status, pageNum, pageSize);
        return RestBean.success(page);
    }

    @Operation(summary = "查询可用优惠券（下单时）")
    @GetMapping("/applicable")
    public RestBean<List<UserCouponVO>> getApplicableCoupons(
            @RequestParam Long userId,
            @RequestParam BigDecimal totalAmount,
            @RequestParam List<Long> productIds,
            @RequestParam List<Long> categoryIds) {
        List<UserCouponVO> list = couponService.getApplicableCoupons(
                userId, totalAmount, productIds, categoryIds);
        return RestBean.success(list);
    }

    @Operation(summary = "使用优惠券")
    @PostMapping("/use")
    public RestBean<Boolean> useCoupon(@Valid @RequestBody UseCouponDTO dto) {
        boolean success = couponService.useCoupon(dto);
        return RestBean.success(success);
    }

    @Operation(summary = "退还优惠券（取消订单）")
    @PostMapping("/refund")
    public RestBean<Boolean> refundCoupon(
            @RequestParam Long userCouponId,
            @RequestParam Long orderId) {
        boolean success = couponService.refundCoupon(userCouponId, orderId);
        return RestBean.success(success);
    }

    @Operation(summary = "预热优惠券库存")
    @PostMapping("/preheat/{couponId}")
    public RestBean<Void> preheatCoupon(@PathVariable Long couponId) {
        couponService.preheatCouponStock(couponId);
        return RestBean.success(null);
    }
}