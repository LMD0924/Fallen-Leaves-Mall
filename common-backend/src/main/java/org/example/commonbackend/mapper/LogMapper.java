package org.example.commonbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;
import org.example.commonbackend.entity.MallOperationLog;

import java.time.LocalDateTime;

/*
 * @Author:总会落叶
 * @Date:2026/3/3
 * @Description:
 */
@Mapper
public interface LogMapper extends BaseMapper<MallOperationLog> {

    /**
     * 删除指定时间之前的日志
     * @param cutoffTime 截止时间
     * @return 删除的记录数
     */
    @Delete("DELETE FROM mall_operation_log WHERE create_time < #{cutoffTime}")
    int deleteByCreateTimeBefore(@Param("cutoffTime") LocalDateTime cutoffTime);
}

