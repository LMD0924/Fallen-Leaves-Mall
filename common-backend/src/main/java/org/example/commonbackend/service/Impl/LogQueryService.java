package org.example.commonbackend.service.Impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commonbackend.entity.MallOperationLog;
import org.example.commonbackend.mapper.LogMapper;
import org.example.commonbackend.redis.LogRedisKey;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 * @Author:总会落叶
 * @Date:2026/3/3
 * @Description: 查询服务 redis优先
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogQueryService {

    private final RedisTemplate<String,String> redisTemplate;

    private final LogMapper operatorLogMapper;

    /*
    * 根据UUID查询日志
    * */
    public MallOperationLog getByUuid(String logUuid){
        //先查redis
        String detailKey = String.format(LogRedisKey.LOG_DETAIL,logUuid);
        String logJson = redisTemplate.opsForValue().get(detailKey);

        if(logJson != null){
            log.debug("redis命中日志：{}",logUuid);
            return JSON.parseObject(logJson,MallOperationLog.class);
        }

        //redis未命中，查数据库
        MallOperationLog log = operatorLogMapper.selectById(logUuid);

        //如果查到并且是三天内的数据，回填redis
        if(log!=null && isWithinThreeDays(log.getCreateTime())){
            String json = JSON.toJSONString(log);
            redisTemplate.opsForValue().set(detailKey,json,LogRedisKey.TTL_THREE_DAYS, TimeUnit.SECONDS);
        }
        return log;
    }

    /*
    * 根据业务ID查询日志
    * */
    public List<MallOperationLog> getByBusinessId(String businessModule,String businessId,int limit){
        String bizKey = String.format(LogRedisKey.LOG_BIZ_LIST,businessModule,businessId);

        //从redis中获取UUID
        List<String> logUuids = redisTemplate.opsForList().range(bizKey,0,limit-1);
        if(logUuids!=null&& !logUuids.isEmpty()){
            //批量获取详情
            List<MallOperationLog> logs = new ArrayList<>();
            for(String uuid: logUuids){
                MallOperationLog log = getByUuid(uuid);
                if(log!=null){
                    logs.add(log);
                }
            }
            return logs;
        }

        //redis未命中，查数据库
        QueryWrapper<MallOperationLog> wrapper = new QueryWrapper<>();
        wrapper.eq("business_module",businessModule)
                .eq("business_id",businessId)
                .last("limit "+limit);
        return operatorLogMapper.selectList(wrapper);
    }

    /*
    * 判断时间是否在三天内
    * */
    private boolean isWithinThreeDays(LocalDateTime time) {
        if (time == null) return false;
        return time.isAfter(LocalDateTime.now().minusDays(3));
    }
}
