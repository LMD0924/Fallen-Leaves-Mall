package org.example.couponservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.couponservice.entity.CouponScope;

import java.util.List;

@Mapper
public interface CouponScopeMapper extends BaseMapper<CouponScope> {

    /**
     * 根据优惠券ID查询适用范围
     */
    @Select("SELECT * FROM coupon_scope WHERE coupon_id = #{couponId}")
    List<CouponScope> selectByCouponId(@Param("couponId") Long couponId);

    /**
     * 根据优惠券ID和范围类型查询
     */
    @Select("SELECT * FROM coupon_scope WHERE coupon_id = #{couponId} AND scope_type = #{scopeType}")
    List<CouponScope> selectByCouponIdAndType(@Param("couponId") Long couponId,
                                              @Param("scopeType") Integer scopeType);

    /**
     * 批量插入适用范围
     */
    int batchInsert(@Param("list") List<CouponScope> scopeList);

    /**
     * 根据优惠券ID删除适用范围
     */
    @Select("DELETE FROM coupon_scope WHERE coupon_id = #{couponId}")
    int deleteByCouponId(@Param("couponId") Long couponId);
}