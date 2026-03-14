package org.example.couponservice.client;

import org.example.couponservice.dto.UseCouponDTO;
import org.example.couponservice.vo.UserCouponVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

/*
 * @Author:总会落叶
 * @Date:2026/3/13
 * @Description:
 */
@FeignClient(name = "coupon-service", path = "/api/coupon")
public interface CouponFeignClient {

    @PostMapping("/use")
    Boolean useCoupon(@RequestBody UseCouponDTO dto);

    @PostMapping("/refund")
    Boolean refundCoupon(@RequestParam("userCouponId") Long userCouponId,
                         @RequestParam("orderId") Long orderId);

    @GetMapping("/applicable")
    List<UserCouponVO> getApplicableCoupons(@RequestParam("userId") Long userId,
                                            @RequestParam("totalAmount") BigDecimal totalAmount,
                                            @RequestParam("productIds") List<Long> productIds,
                                            @RequestParam("categoryIds") List<Long> categoryIds);
}
