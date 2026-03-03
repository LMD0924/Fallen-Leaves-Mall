package org.example.commonbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.commonbackend.entity.MallOperationLog;

/*
 * @Author:总会落叶
 * @Date:2026/3/3
 * @Description:
 */
@Mapper
public interface LogMapper extends BaseMapper<MallOperationLog> {
}
