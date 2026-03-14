package org.example.seckillsession.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.seckillsession.entity.SeckillOrder;

@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {

    /**
     * 查询用户是否已秒杀该商品
     */
    @Select("SELECT COUNT(*) FROM seckill_order " +
            "WHERE user_id = #{userId} AND seckill_id = #{seckillId} " +
            "AND status IN (0, 1)")
    int countUserSeckill(@Param("userId") Long userId,
                         @Param("seckillId") Long seckillId);

    /**
     * 更新为已支付
     */
    @Update("UPDATE seckill_order SET status = 1, pay_time = NOW(), " +
            "order_id = #{orderId}, order_no = #{orderNo} " +
            "WHERE id = #{id} AND status = 0")
    int updatePaid(@Param("id") Long id,
                   @Param("orderId") Long orderId,
                   @Param("orderNo") String orderNo);

    /**
     * 取消超时未支付订单
     */
    @Update("UPDATE seckill_order SET status = 3, cancel_time = NOW() " +
            "WHERE status = 0 AND create_time < DATE_SUB(NOW(), INTERVAL 15 MINUTE)")
    int cancelExpiredOrders();
}