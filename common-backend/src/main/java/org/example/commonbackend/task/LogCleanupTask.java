package org.example.commonbackend.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commonbackend.entity.MallOperationLog;
import org.example.commonbackend.mapper.LogMapper;
import org.example.commonbackend.redis.LogRedisKey;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogCleanupTask {

    private final LogMapper logMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int RETENTION_DAYS = 30; // 日志保留天数

    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨执行
    public void cleanupExpiredLogs() {
        try {
            log.info("开始清理过期日志");
            
            // 清理数据库中的过期日志
            cleanupDatabaseLogs();
            
            // 清理Redis中的过期日志
            cleanupRedisLogs();
            
            log.info("过期日志清理完成");
        } catch (Exception e) {
            log.error("清理过期日志失败", e);
        }
    }

    private void cleanupDatabaseLogs() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(RETENTION_DAYS);
        int deleted = logMapper.deleteByCreateTimeBefore(cutoffTime);
        log.info("清理数据库过期日志 {} 条", deleted);
    }

    private void cleanupRedisLogs() {
        // 清理日志详情
        Set<String> detailKeys = redisTemplate.keys("log:detail:*");
        if (detailKeys != null) {
            for (String key : detailKeys) {
                // 检查是否过期
                Boolean exists = redisTemplate.hasKey(key);
                if (exists != null && !exists) {
                    redisTemplate.delete(key);
                }
            }
        }
        
        // 清理业务ID索引
        Set<String> bizKeys = redisTemplate.keys("log:biz:*");
        if (bizKeys != null) {
            for (String key : bizKeys) {
                Boolean exists = redisTemplate.hasKey(key);
                if (exists != null && !exists) {
                    redisTemplate.delete(key);
                }
            }
        }
        
        // 清理操作者索引
        Set<String> operatorKeys = redisTemplate.keys("log:operator:*");
        if (operatorKeys != null) {
            for (String key : operatorKeys) {
                Boolean exists = redisTemplate.hasKey(key);
                if (exists != null && !exists) {
                    redisTemplate.delete(key);
                }
            }
        }
        
        // 清理日期索引
        Set<String> dateKeys = redisTemplate.keys("log:date:*");
        if (dateKeys != null) {
            for (String key : dateKeys) {
                Boolean exists = redisTemplate.hasKey(key);
                if (exists != null && !exists) {
                    redisTemplate.delete(key);
                }
            }
        }
        
        log.info("清理Redis过期日志完成");
    }
}
