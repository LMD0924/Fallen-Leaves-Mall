package org.example.couponservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.couponservice.entity.CouponReceiveLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface CouponReceiveLogMapper extends BaseMapper<CouponReceiveLog> {

    /**
     * 查询用户领取记录
     */
    @Select("SELECT * FROM coupon_receive_log WHERE user_id = #{userId} ORDER BY receive_time DESC")
    List<CouponReceiveLog> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询优惠券领取记录
     */
    @Select("SELECT * FROM coupon_receive_log WHERE coupon_id = #{couponId} ORDER BY receive_time DESC")
    List<CouponReceiveLog> selectByCouponId(@Param("couponId") Long couponId);

    /**
     * 统计今日领取次数
     */
    @Select("SELECT COUNT(*) FROM coupon_receive_log WHERE coupon_id = #{couponId} " +
            "AND receive_time >= #{startTime}")
    int countTodayReceive(@Param("couponId") Long couponId,
                          @Param("startTime") LocalDateTime startTime);

    /**
     * 统计用户领取成功/失败次数
     */
    @Select("SELECT status, COUNT(*) as count FROM coupon_receive_log " +
            "WHERE user_id = #{userId} GROUP BY status")
    List<Map<String, Object>> countByUserStatus(@Param("userId") Long userId);

    /**
     * 统计优惠券领取成功/失败次数
     */
    @Select("SELECT status, COUNT(*) as count FROM coupon_receive_log " +
            "WHERE coupon_id = #{couponId} GROUP BY status")
    List<Map<String, Object>> countByCouponStatus(@Param("couponId") Long couponId);

    /**
     * 查询失败日志
     */
    @Select("SELECT * FROM coupon_receive_log WHERE coupon_id = #{couponId} AND status = 2")
    List<CouponReceiveLog> selectFailedLogs(@Param("couponId") Long couponId);

    /**
     * 统计IP领取次数（防刷）
     */
    @Select("SELECT COUNT(*) FROM coupon_receive_log WHERE ip = #{ip} " +
            "AND receive_time >= #{startTime}")
    int countByIp(@Param("ip") String ip,
                  @Param("startTime") LocalDateTime startTime);

    /**
     * 清理指定时间前的日志
     */
    @Select("DELETE FROM coupon_receive_log WHERE receive_time < #{beforeTime}")
    int deleteLogsBefore(@Param("beforeTime") LocalDateTime beforeTime);
}