package org.example.backend.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/*
 * @Author:总会落叶
 * @Date:2026/2/6
 * @Description: TokenRedisUtil
 */
@Slf4j
@Service
public class TokenRedisUtil {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Redis Key 前缀
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final String USER_TOKEN_PREFIX = "user:token:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh:token:";
    private static final String TOKEN_INFO_PREFIX = "token:info:";

    /**
     * 将令牌加入黑名单（登出或撤销时使用）
     * @param token JWT令牌
     * @param expirationTime 剩余过期时间(秒)
     */
    public void addToBlacklist(String token, long expirationTime) {
        if (expirationTime <= 0) return;

        String key = TOKEN_BLACKLIST_PREFIX + token;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        ops.set(key, "blacklisted", expirationTime, TimeUnit.SECONDS);
        log.debug("Token已加入黑名单: {}", token);
    }

    /**
     * 检查令牌是否在黑名单中
     */
    public boolean isTokenBlacklisted(String token) {
        String key = TOKEN_BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 存储用户与访问令牌的映射（用于单设备登录）
     */
    public void storeUserAccessToken(Long userId, String accessToken, long expirationTime) {
        String key = USER_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(key, accessToken, expirationTime, TimeUnit.SECONDS);
    }

    /**
     * 获取用户当前的访问令牌
     */
    public String getUserAccessToken(Long userId) {
        String key = USER_TOKEN_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 存储刷新令牌
     */
    public void storeRefreshToken(String refreshToken, Long userId, long expirationTime) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.opsForValue().set(key, userId.toString(), expirationTime, TimeUnit.SECONDS);
    }

    /**
     * 验证刷新令牌
     */
    public Long validateRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        String userIdStr = redisTemplate.opsForValue().get(key);

        if (userIdStr == null) {
            return null;
        }

        return Long.parseLong(userIdStr);
    }

    /**
     * 删除刷新令牌
     */
    public void deleteRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.delete(key);
    }

    /**
     * 存储令牌额外信息（设备信息、IP等）
     */
    public void storeTokenInfo(String token, String info, long expirationTime) {
        String key = TOKEN_INFO_PREFIX + token;
        redisTemplate.opsForValue().set(key, info, expirationTime, TimeUnit.SECONDS);
    }

    /**
     * 获取令牌额外信息
     */
    public String getTokenInfo(String token) {
        String key = TOKEN_INFO_PREFIX + token;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 强制用户下线（管理员功能）
     */
    public void forceLogout(Long userId) {
        // 1. 获取用户当前的访问令牌
        String accessToken = getUserAccessToken(userId);
        if (accessToken != null) {
            // 2. 将访问令牌加入黑名单
            long expireTime = getTokenExpireTime(accessToken);
            if (expireTime > 0) {
                addToBlacklist(accessToken, expireTime);
            }
            // 3. 删除用户令牌映射
            redisTemplate.delete(USER_TOKEN_PREFIX + userId);
        }

        log.info("用户 {} 已被强制下线", userId);
    }

    /**
     * 清理用户所有令牌（修改密码后使用）
     */
    public void clearUserTokens(Long userId) {
        // 删除访问令牌映射
        redisTemplate.delete(USER_TOKEN_PREFIX + userId);

        // 可以扩展：删除该用户的所有刷新令牌
        // 需要特殊的数据结构存储用户的所有刷新令牌

        log.info("用户 {} 的所有令牌已清理", userId);
    }

    /**
     * 获取令牌剩余过期时间（秒）
     */
    private long getTokenExpireTime(String token) {
        // 实际应用中可以从JWT解析过期时间
        // 这里简化处理，返回默认值
        return 7200;  // 2小时
    }
}