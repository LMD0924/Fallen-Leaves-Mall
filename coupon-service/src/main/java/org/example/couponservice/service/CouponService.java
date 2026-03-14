package org.example.couponservice.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.couponservice.dto.ReceiveCouponDTO;
import org.example.couponservice.dto.UseCouponDTO;
import org.example.couponservice.vo.CouponVO;
import org.example.couponservice.vo.UserCouponVO;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {

    /**
     * 领取优惠券（高并发）
     */
    UserCouponVO receiveCoupon(ReceiveCouponDTO dto);

    /**
     * 查询可领取优惠券列表
     */
    Page<CouponVO> getAvailableCoupons(Integer pageNum, Integer pageSize);

    /**
     * 查询用户优惠券列表
     */
    Page<UserCouponVO> getUserCoupons(Long userId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 查询用户可用优惠券（下单时调用）
     */
    List<UserCouponVO> getApplicableCoupons(Long userId, BigDecimal totalAmount,
                                            List<Long> productIds, List<Long> categoryIds);

    /**
     * 使用优惠券
     */
    boolean useCoupon(UseCouponDTO dto);

    /**
     * 退还优惠券（取消订单时）
     */
    boolean refundCoupon(Long userCouponId, Long orderId);

    /**
     * 预热优惠券库存到Redis
     */
    void preheatCouponStock(Long couponId);
}