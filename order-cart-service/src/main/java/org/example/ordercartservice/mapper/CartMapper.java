package org.example.ordercartservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.example.ordercartservice.entity.Cart;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    /**
     * 批量更新选中状态
     */
    @Update("UPDATE cart SET selected = #{selected} WHERE user_id = #{userId} " +
            "AND id IN (<foreach collection='cartIds' item='id' separator=','>#{id}</foreach>)")
    int updateSelected(@Param("userId") Long userId,
                       @Param("cartIds") List<Long> cartIds,
                       @Param("selected") Boolean selected);

    /**
     * 清空购物车（下单后删除已选中的商品）
     */
    @Update("DELETE FROM cart WHERE user_id = #{userId} AND selected = 1")
    int clearSelected(@Param("userId") Long userId);
}