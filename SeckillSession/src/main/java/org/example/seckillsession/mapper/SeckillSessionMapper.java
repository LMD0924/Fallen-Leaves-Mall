package org.example.seckillsession.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.seckillsession.entity.SeckillSession;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SeckillSessionMapper extends BaseMapper<SeckillSession> {

    /**
     * 更新场次状态
     */
    @Update("UPDATE seckill_session SET status = #{status} " +
            "WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 查询当前进行中的场次
     */
    @Select("SELECT * FROM seckill_session " +
            "WHERE start_time <= NOW() AND end_time >= NOW() " +
            "ORDER BY start_time ASC")
    List<SeckillSession> selectCurrentSessions();

    /**
     * 查询即将开始的场次
     */
    @Select("SELECT * FROM seckill_session " +
            "WHERE start_time > NOW() AND start_time <= DATE_ADD(NOW(), INTERVAL 1 HOUR) " +
            "ORDER BY start_time ASC")
    List<SeckillSession> selectUpcomingSessions();
}