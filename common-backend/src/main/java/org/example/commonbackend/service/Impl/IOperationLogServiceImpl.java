package org.example.commonbackend.service.Impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.commonbackend.entity.MallOperationLog;
import org.example.commonbackend.mapper.LogMapper;
import org.example.commonbackend.redis.LogRedisKey;
import org.example.commonbackend.service.IOperationLogService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/*
 * @Author:总会落叶
 * @Date:2026/3/3
 * @Description:
 */
@Service
@RequiredArgsConstructor
public class IOperationLogServiceImpl extends ServiceImpl<LogMapper, MallOperationLog> implements IOperationLogService {

    private final LogMapper operationLogMapper;

    private final RedisTemplate<String,String> redisTemplate;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Async("logTaskExecutor")
    @Override
    public void saveAsync(MallOperationLog log) {
        save(log);
    }

    @Override
    public boolean save(MallOperationLog log1) {
        try {
            //保存到数据库
            int result=operationLogMapper.insert(log1);

            //保存到redis
            saveRedis(log1);
            return result>0;
        } catch (Exception e) {
            log.error("保存操作日志到数据库失败", e);
            // 可以添加备选方案：写入本地文件或发送到消息队列
            try{
                saveRedis(log1);
            }catch(Exception e1){
                log.error("保存操作日志到redis失败", e1);
            }
        }
        return false;
    }

    /*
    * 保存到redis
    * */
    private void saveRedis(MallOperationLog log){
        String logJson = JSON.toJSONString(log);
        String logUuid = log.getLogUuid();

        //保存日志详情
        String detailKey = String.format(LogRedisKey.LOG_DETAIL, logUuid);
        redisTemplate.opsForValue().set(detailKey,logJson, LogRedisKey.TTL_THREE_DAYS, TimeUnit.SECONDS);

        //按业务ID索引
        if(log.getBusinessModule()!=null&&log.getBusinessId()!=null){
            String bizKey = String.format(LogRedisKey.LOG_BIZ_LIST, log.getBusinessModule(), log.getBusinessId());
            redisTemplate.opsForList().leftPush(bizKey, logUuid);
            redisTemplate.expire(bizKey,LogRedisKey.TTL_THREE_DAYS,TimeUnit.SECONDS);

            //只保留最近100条
            redisTemplate.opsForList().trim(bizKey,0,99);
        }

        //按操作者索引
        if(log.getOperatorId()!=null){
            String operatorKey = String.format(LogRedisKey.LOG_OPERATOR_LIST,log.getOperatorType(), log.getOperatorId());
            redisTemplate.opsForList().leftPush(operatorKey, logUuid);
            redisTemplate.expire(operatorKey,LogRedisKey.TTL_THREE_DAYS,TimeUnit.SECONDS);
            redisTemplate.opsForList().trim(operatorKey,0,99);
        }

        // 按日期索引（用于按时间查询）
        String dateStr = log.getCreateTime().format(DATE_FORMAT);
        String dateKey = String.format(LogRedisKey.LOG_DATE_INDEX, dateStr, log.getLogType());
        redisTemplate.opsForList().leftPush(dateKey, logUuid);
        redisTemplate.expire(dateKey, LogRedisKey.TTL_THREE_DAYS, TimeUnit.SECONDS);
        redisTemplate.opsForList().trim(dateKey, 0, 499); // 每天最多500条
    }

}
