package org.example.couponservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.couponservice.entity.Coupon;

import java.util.List;

@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

    /**
     * 扣减库存（乐观锁）
     */
    @Update("UPDATE coupon SET stock = stock - 1, receive_count = receive_count + 1 " +
            "WHERE id = #{couponId} AND stock > 0")
    int deductStock(@Param("couponId") Long couponId);

    /**
     * 增加库存（取消领取时恢复）
     */
    @Update("UPDATE coupon SET stock = stock + 1, receive_count = receive_count - 1 " +
            "WHERE id = #{couponId}")
    int increaseStock(@Param("couponId") Long couponId);

    /**
     * 查询可领取的优惠券
     */
    @Select("SELECT * FROM coupon WHERE status = 1 " +
            "AND receive_start_time <= NOW() " +
            "AND receive_end_time >= NOW() " +
            "AND stock > 0 " +
            "ORDER BY create_time DESC")
    List<Coupon> selectAvailableCoupons();
}