package org.example.backend.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.controller.VO.LoginResultVO;
import org.example.backend.entity.User;
import org.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务
 */
@Slf4j
@Service
public class AuthServiceUtil {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private TokenRedisUtil tokenRedisUtil;

    @Autowired
    private UserService userService;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 用户登录
     */
    public LoginResultVO login(String account, String password,
                               String deviceInfo, String ipAddress) {

        // 1. 验证用户名密码
        User user = userService.login(account, password);

        // 2. 检查是否已登录（单设备登录）
        String existingToken = tokenRedisUtil.getUserAccessToken(user.getId());
        if (existingToken != null) {
            // 将旧令牌加入黑名单
            long expireTime = expiration;
            tokenRedisUtil.addToBlacklist(existingToken, expireTime);
            log.info("用户 {} 已有登录，旧令牌已加入黑名单", account);
        }

        // 3. 生成访问令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("email", user.getEmail());

        String accessToken = jwtTokenUtil.generateAccessToken(
                user.getId(), user.getUsername(), claims
        );

        // 4. 生成刷新令牌
        String refreshToken = jwtTokenUtil.generateRefreshToken(user.getId());

        // 5. 存储到Redis
        // 存储访问令牌映射
        tokenRedisUtil.storeUserAccessToken(user.getId(), accessToken, expiration);

        // 存储刷新令牌
        tokenRedisUtil.storeRefreshToken(refreshToken, user.getId(), expiration * 24 * 30);

        // 存储令牌额外信息
        String tokenInfo = String.format("device:%s,ip:%s", deviceInfo, ipAddress);
        tokenRedisUtil.storeTokenInfo(accessToken, tokenInfo, expiration);

        // 6. 返回结果
        return LoginResultVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .accessToken(accessToken)
                //.refreshToken(refreshToken)
                .expiresIn(expiration)
                .tokenType("Bearer")
                .role(user.getRole())
                .build();
    }

    /**
     * 用户登出
     */
    public void logout(String accessToken, Long userId) {
        if (accessToken != null && !accessToken.isEmpty()) {
            // 将访问令牌加入黑名单
            long expireTime = getRemainingExpireTime(accessToken);
            if (expireTime > 0) {
                tokenRedisUtil.addToBlacklist(accessToken, expireTime);
            }
        }

        if (userId != null) {
            // 删除用户令牌映射
            tokenRedisUtil.getUserAccessToken(userId);
            // 注意：这里不删除刷新令牌，让刷新令牌自然过期
        }

        log.info("用户 {} 已登出", userId);
    }

    /**
     * 刷新访问令牌
     */
    public LoginResultVO refreshToken(String refreshToken, HttpServletRequest request) {
        // 1. 验证刷新令牌
        Long userId = tokenRedisUtil.validateRefreshToken(refreshToken);
        if (userId == null) {
            throw new IllegalArgumentException("刷新令牌无效或已过期");
        }

        // 2. 获取用户信息
        LoginResultVO user = userService.selectUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 3. 删除旧的刷新令牌（可选：实现刷新令牌一次性使用）
        // tokenRedisUtil.deleteRefreshToken(refreshToken);

        // 4. 生成新的访问令牌和刷新令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("email", user.getEmail());

        String newAccessToken = jwtTokenUtil.generateAccessToken(
                user.getId(), user.getUsername(), claims
        );

        String newRefreshToken = jwtTokenUtil.generateRefreshToken(user.getId());

        // 5. 存储到Redis
        tokenRedisUtil.storeUserAccessToken(user.getId(), newAccessToken, expiration);
        tokenRedisUtil.storeRefreshToken(newRefreshToken, user.getId(), expiration * 24 * 30);

        // 6. 返回结果
/*        return LoginResultVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .accessToken(newAccessToken)
                //.refreshToken(newRefreshToken)
                .expiresIn(expiration)
                .tokenType("Bearer")
                .role(user.getRole())
                .build();*/
        return null;
    }

    /**
     * 验证访问令牌
     */
    public Long validateAccessToken(String token) {
        // 1. 检查是否在黑名单中
        if (tokenRedisUtil.isTokenBlacklisted(token)) {
            log.warn("令牌已被加入黑名单: {}", token);
            return null;
        }

        // 2. 验证JWT令牌
        if (!jwtTokenUtil.validateToken(token)) {
            return null;
        }

        // 3. 获取用户ID
        Long userId = jwtTokenUtil.getUserIdFromToken(token);

        // 4. 检查令牌是否与Redis中存储的一致（单设备登录验证）
        String storedToken = tokenRedisUtil.getUserAccessToken(userId);
        if (storedToken == null || !storedToken.equals(token)) {
            log.warn("令牌不匹配，可能在其他设备登录: userId={}", userId);
            // 将当前令牌加入黑名单
            long expireTime = getRemainingExpireTime(token);
            if (expireTime > 0) {
                tokenRedisUtil.addToBlacklist(token, expireTime);
            }
            return null;
        }

        return userId;
    }

    /**
     * 获取令牌剩余过期时间
     */
    private long getRemainingExpireTime(String token) {
        try {
            Date expiration = jwtTokenUtil.getExpirationDateFromToken(token);
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return remaining > 0 ? remaining / 1000 : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}