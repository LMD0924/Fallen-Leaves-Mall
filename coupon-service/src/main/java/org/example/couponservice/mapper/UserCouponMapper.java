package org.example.couponservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.couponservice.entity.UserCoupon;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {

    /**
     * 统计用户已领取数量
     */
    @Select("SELECT COUNT(*) FROM user_coupon " +
            "WHERE user_id = #{userId} AND coupon_id = #{couponId}")
    int countUserReceived(@Param("userId") Long userId,
                          @Param("couponId") Long couponId);

    /**
     * 查询用户可用优惠券
     */
    @Select("SELECT * FROM user_coupon " +
            "WHERE user_id = #{userId} " +
            "AND status = 0 " +
            "AND start_time <= NOW() " +
            "AND end_time >= NOW() " +
            "ORDER BY face_value DESC")
    List<UserCoupon> selectUserAvailableCoupons(@Param("userId") Long userId);

    /**
     * 查询用户可用优惠券（按商品筛选）
     */
    @Select("SELECT uc.* FROM user_coupon uc " +
            "LEFT JOIN coupon_scope cs ON uc.coupon_id = cs.coupon_id " +
            "WHERE uc.user_id = #{userId} " +
            "AND uc.status = 0 " +
            "AND uc.start_time <= NOW() " +
            "AND uc.end_time >= NOW() " +
            "AND (uc.condition IS NULL OR uc.condition <= #{totalAmount}) " +
            "AND (cs.scope_id IS NULL OR cs.scope_id = #{productId} OR cs.scope_id = #{categoryId})")
    List<UserCoupon> selectApplicableCoupons(@Param("userId") Long userId,
                                             @Param("totalAmount") BigDecimal totalAmount,
                                             @Param("productId") Long productId,
                                             @Param("categoryId") Long categoryId);

    /**
     * 使用优惠券
     */
    @Update("UPDATE user_coupon SET status = 1, use_time = NOW(), " +
            "order_id = #{orderId}, order_no = #{orderNo} " +
            "WHERE id = #{userCouponId} AND status = 0")
    int useCoupon(@Param("userCouponId") Long userCouponId,
                  @Param("orderId") Long orderId,
                  @Param("orderNo") String orderNo);

    /**
     * 批量过期优惠券（定时任务用）
     */
    @Update("UPDATE user_coupon SET status = 2 " +
            "WHERE status = 0 AND end_time < NOW()")
    int batchExpireCoupons();
}