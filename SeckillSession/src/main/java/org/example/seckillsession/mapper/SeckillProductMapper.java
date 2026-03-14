package org.example.seckillsession.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.seckillsession.entity.SeckillProduct;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SeckillProductMapper extends BaseMapper<SeckillProduct> {

    /**
     * 扣减秒杀库存（乐观锁）
     */
    @Update("UPDATE seckill_product SET seckill_stock = seckill_stock - 1, " +
            "sold_count = sold_count + 1 " +
            "WHERE id = #{seckillId} AND seckill_stock > 0")
    int deductStock(@Param("seckillId") Long seckillId);

    /**
     * 根据场次查询秒杀商品
     */
    @Select("SELECT sp.*, ss.start_time, ss.end_time FROM seckill_product sp " +
            "LEFT JOIN seckill_session ss ON sp.session_id = ss.id " +
            "WHERE sp.session_id = #{sessionId} AND sp.status = 1 " +
            "AND ss.status = 1 AND ss.start_time <= NOW() AND ss.end_time >= NOW() " +
            "ORDER BY sp.sort_order ASC")
    List<SeckillProduct> selectBySession(@Param("sessionId") Long sessionId);

    /**
     * 查询当前进行的秒杀商品
     */
    @Select("SELECT sp.* FROM seckill_product sp " +
            "LEFT JOIN seckill_session ss ON sp.session_id = ss.id " +
            "WHERE ss.status = 1 AND ss.start_time <= NOW() AND ss.end_time >= NOW() " +
            "AND sp.status = 1 AND sp.seckill_stock > 0")
    List<SeckillProduct> selectCurrentSeckill();
}