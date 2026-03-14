package org.example.ordercartservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.ordercartservice.entity.Order;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 更新订单状态（带乐观锁）
     */
    @Update("UPDATE `order` SET status = #{newStatus} " +
            "WHERE id = #{orderId} AND status = #{oldStatus}")
    int updateStatus(@Param("orderId") Long orderId,
                     @Param("oldStatus") Integer oldStatus,
                     @Param("newStatus") Integer newStatus);

    /**
     * 获取用户最新订单号
     */
    @Select("SELECT order_no FROM `order` WHERE user_id = #{userId} " +
            "ORDER BY create_time DESC LIMIT 1")
    String getLastOrderNo(@Param("userId") Long userId);
}