package org.example.backend.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.entity.RefreshTokenInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * TokenRedisUtil - 使用StringRedisTemplate
 * @Author:总会落叶
 * @Date:2026/2/6
 */
@Slf4j
@Service
public class TokenRedisUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;  // 使用 StringRedisTemplate

    // Redis Key 前缀
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final String USER_TOKEN_PREFIX = "user:token:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final String TOKEN_INFO_PREFIX = "token:info:";
    private static final String USER_SESSIONS_PREFIX = "user:sessions:";

    /**
     * 将令牌加入黑名单
     */
    public void addToBlacklist(String token, long expirationTime) {
        if (expirationTime <= 0) return;

        String key = TOKEN_BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "blacklisted", expirationTime, TimeUnit.SECONDS);
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
     * 存储Refresh Token信息
     */
    public void storeRefreshTokenInfo(String tokenId, RefreshTokenInfo info, long expirationTime) {
        String key = REFRESH_TOKEN_PREFIX + tokenId;
        // 将对象转换为JSON字符串存储
        String jsonStr = JSON.toJSONString(info);
        redisTemplate.opsForValue().set(key, jsonStr, expirationTime, TimeUnit.SECONDS);

        // 同时记录用户的所有会话
        String sessionsKey = USER_SESSIONS_PREFIX + info.getUserId();
        redisTemplate.opsForSet().add(sessionsKey, tokenId);
        redisTemplate.expire(sessionsKey, 30, TimeUnit.DAYS);

        log.debug("Refresh Token已存储: {}", tokenId);
    }

    /**
     * 获取Refresh Token信息
     */
    public RefreshTokenInfo getRefreshTokenInfo(String tokenId) {
        String key = REFRESH_TOKEN_PREFIX + tokenId;
        String jsonStr = redisTemplate.opsForValue().get(key);
        if (jsonStr == null) {
            return null;
        }

        // 将JSON字符串转换回对象
        return JSON.parseObject(jsonStr, RefreshTokenInfo.class);
    }

    /**
     * 删除Refresh Token信息
     */
    public void deleteRefreshTokenInfo(String tokenId) {
        String key = REFRESH_TOKEN_PREFIX + tokenId;
        redisTemplate.delete(key);
    }

    /**
     * 存储用户与访问令牌的映射
     */
    public void storeUserAccessToken(Long userId, String accessToken, long expirationTime) {
        String key = USER_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(key, accessToken, expirationTime, TimeUnit.SECONDS);

        // 同时存储反向映射（通过token查用户）
        String tokenKey = TOKEN_INFO_PREFIX + accessToken;
        redisTemplate.opsForValue().set(tokenKey, userId.toString(), expirationTime, TimeUnit.SECONDS);
    }

    /**
     * 获取用户当前的访问令牌
     */
    public String getUserAccessToken(Long userId) {
        String key = USER_TOKEN_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 通过Access Token获取用户ID
     */
    public Long getUserIdByAccessToken(String accessToken) {
        String key = TOKEN_INFO_PREFIX + accessToken;
        String userIdStr = redisTemplate.opsForValue().get(key);
        if (userIdStr == null) {
            return null;
        }
        return Long.parseLong(userIdStr);
    }

    /**
     * 获取用户的所有会话（返回Set<String>）
     */
    public Set<String> getUserSessions(Long userId) {
        String key = USER_SESSIONS_PREFIX + userId;
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 获取用户会话数量
     */
    public Long getUserSessionCount(Long userId) {
        String key = USER_SESSIONS_PREFIX + userId;
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 删除用户会话记录
     */
    public void removeUserSession(Long userId, String tokenId) {
        String key = USER_SESSIONS_PREFIX + userId;
        redisTemplate.opsForSet().remove(key, tokenId);
    }

    /**
     * 存储令牌额外信息
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
     * 限制并发登录数量
     */
    public boolean checkAndLimitSessions(Long userId, int maxSessions, String newTokenId) {
        String sessionsKey = USER_SESSIONS_PREFIX + userId;

        // 获取当前会话数量
        Long currentCount = redisTemplate.opsForSet().size(sessionsKey);

        if (currentCount == null || currentCount < maxSessions) {
            // 未达到上限，直接添加
            redisTemplate.opsForSet().add(sessionsKey, newTokenId);
            redisTemplate.expire(sessionsKey, 30, TimeUnit.DAYS);
            return true;
        } else {
            // 达到上限，移除最早的会话
            Set<String> sessions = redisTemplate.opsForSet().members(sessionsKey);
            if (sessions != null && !sessions.isEmpty()) {
                String oldestTokenId = sessions.iterator().next();

                // 删除最早的Refresh Token
                deleteRefreshTokenInfo(oldestTokenId);

                // 从会话集中移除
                redisTemplate.opsForSet().remove(sessionsKey, oldestTokenId);

                // 添加新会话
                redisTemplate.opsForSet().add(sessionsKey, newTokenId);

                log.info("用户 {} 会话数达到上限，已移除最旧会话: {}", userId, oldestTokenId);
            }
            return true;
        }
    }

    /**
     * 添加用户会话记录
     */
    public void addUserSession(Long userId, String tokenId) {
        String key = USER_SESSIONS_PREFIX + userId;
        redisTemplate.opsForSet().add(key, tokenId);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }

    /**
     * 强制用户下线（踢除指定设备）
     */
    public void forceLogoutUserDevice(Long userId, String tokenId) {
        // 1. 获取Refresh Token信息
        RefreshTokenInfo info = getRefreshTokenInfo(tokenId);
        if (info != null) {
            // 2. 如果有Access Token，加入黑名单
            String accessToken = getUserAccessToken(userId);
            if (accessToken != null) {
                addToBlacklist(accessToken, 3600);
            }

            // 3. 删除Refresh Token
            deleteRefreshTokenInfo(tokenId);

            // 4. 删除会话记录
            removeUserSession(userId, tokenId);

            // 5. 如果这是当前使用的设备，删除用户令牌映射
            String currentToken = getUserAccessToken(userId);
            if (currentToken != null && currentToken.equals(accessToken)) {
                redisTemplate.delete(USER_TOKEN_PREFIX + userId);
            }
        }
    }

    /**
     * 强制用户所有设备下线
     */
    public void forceLogoutAllDevices(Long userId) {
        // 1. 获取所有会话
        Set<String> sessions = getUserSessions(userId);

        if (sessions != null) {
            // 2. 删除所有Refresh Token
            for (String tokenId : sessions) {
                deleteRefreshTokenInfo(tokenId);
            }
        }

        // 3. 删除会话记录
        redisTemplate.delete(USER_SESSIONS_PREFIX + userId);

        // 4. 删除用户令牌映射
        String accessToken = getUserAccessToken(userId);
        if (accessToken != null) {
            addToBlacklist(accessToken, 3600);
            redisTemplate.delete(USER_TOKEN_PREFIX + userId);

            // 删除token信息
            String tokenKey = TOKEN_INFO_PREFIX + accessToken;
            redisTemplate.delete(tokenKey);
        }

        log.info("用户 {} 所有设备已强制下线", userId);
    }
}