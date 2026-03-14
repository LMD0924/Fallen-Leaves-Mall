package org.example.ordercartservice.common;

import org.example.ordercartservice.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/*
 * @Author:总会落叶
 * @Date:2026/3/13
 * @Description:
 */
@Service
public class CartCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CART_KEY = "cart:user:";

    /**
     * 缓存购物车（过期时间7天）
     */
    public void cacheCart(Long userId, CartVO cart) {
        String key = CART_KEY + userId;
        redisTemplate.opsForValue().set(key, cart, 7, TimeUnit.DAYS);
    }

    /**
     * 获取缓存购物车
     */
    public CartVO getCachedCart(Long userId) {
        String key = CART_KEY + userId;
        return (CartVO) redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     */
    public void removeCartCache(Long userId) {
        String key = CART_KEY + userId;
        redisTemplate.delete(key);
    }
}
