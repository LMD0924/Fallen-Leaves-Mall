package org.example.merchantbackend.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.common.RestBean;
import org.example.merchantbackend.dto.CouponAddDTO;
import org.example.merchantbackend.dto.CouponUpdateDTO;
import org.example.merchantbackend.service.Impl.CouponAdminService;
import org.example.merchantbackend.vo.CouponDetailVO;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台优惠券管理")
@RestController
@RequestMapping("/api/admin/coupon")
@RequiredArgsConstructor
public class CouponAdminController {

    private final CouponAdminService couponAdminService;

    @Operation(summary = "新增优惠券")
    @PostMapping("/add")
    public RestBean<CouponDetailVO> addCoupon(@Valid @RequestBody CouponAddDTO dto) {
        CouponDetailVO coupon = couponAdminService.addCoupon(dto);
        return RestBean.success(coupon);
    }

    @Operation(summary = "更新优惠券")
    @PutMapping("/update")
    public RestBean<CouponDetailVO> updateCoupon(@Valid @RequestBody CouponUpdateDTO dto) {
        CouponDetailVO coupon = couponAdminService.updateCoupon(dto);
        return RestBean.success(coupon);
    }

    @Operation(summary = "删除优惠券")
    @DeleteMapping("/delete/{id}")
    public RestBean<Boolean> deleteCoupon(@PathVariable Long id) {
        boolean success = couponAdminService.deleteCoupon(id);
        return RestBean.success(success);
    }

    @Operation(summary = "上架/下架优惠券")
    @PutMapping("/status/{id}")
    public RestBean<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = couponAdminService.updateCouponStatus(id, status);
        return RestBean.success(success);
    }
}