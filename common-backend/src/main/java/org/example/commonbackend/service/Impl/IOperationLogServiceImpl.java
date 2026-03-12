package org.example.commonbackend.service.Impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 * @Author:总会落叶
 * @Date:2026/3/3
 * @Description:
 */
@Slf4j
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
        String dateKey = String.format(LogRedisKey.LOG_DATE_INDEX, dateStr, log.getLogType().getCode());
        redisTemplate.opsForList().leftPush(dateKey, logUuid);
        redisTemplate.expire(dateKey, LogRedisKey.TTL_THREE_DAYS, TimeUnit.SECONDS);
        redisTemplate.opsForList().trim(dateKey, 0, 499); // 每天最多500条
    }

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
        MallOperationLog log = operationLogMapper.selectById(logUuid);

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
    public List<MallOperationLog> getByBusinessId(String businessModule, String businessId, int limit){
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
        return operationLogMapper.selectList(wrapper);
    }

    /*
     * 判断时间是否在三天内
     * */
    private boolean isWithinThreeDays(LocalDateTime time) {
        if (time == null) return false;
        return time.isAfter(LocalDateTime.now().minusDays(3));
    }

}
