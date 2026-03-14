package org.example.ordercartservice.common;

/*
 * @Author:总会落叶
 * @Date:2026/3/13
 * @Description:
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 订单幂等性校验
 * 使用Redis存储已处理的订单号
 */
@Component
public class OrderIdempotentChecker {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String IDEMPOTENT_KEY = "order:idempotent:";

    /**
     * 检查是否已处理
     */
    public boolean checkAndMark(String orderNo) {
        String key = IDEMPOTENT_KEY + orderNo;
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", 24, TimeUnit.HOURS);
        return success != null && success;
    }

    /**
     * 释放标记（取消订单时）
     */
    public void release(String orderNo) {
        String key = IDEMPOTENT_KEY + orderNo;
        redisTemplate.delete(key);
    }
}