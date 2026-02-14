package org.example.backend.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.controller.VO.LoginResultVO;
import org.example.backend.entity.RefreshTokenInfo;
import org.example.backend.entity.User;
import org.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.IdUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务
 */
@Slf4j
@Service
@Configuration
public class AuthServiceUtil {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private TokenRedisUtil tokenRedisUtil;

    @Autowired
    private UserService userService;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    @Value("${auth.max-sessions-per-user:5}")
    private Integer maxSessionsPerUser;

    @Value("${auth.enable-token-rotation:true}")
    private Boolean enableTokenRotation;

    /**
     * 用户登录
     */
    public LoginResultVO login(String account, String password,
                               String deviceInfo, String ipAddress,
                               String userAgent,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        // 1. 验证用户名密码
        User user = userService.login(account, password);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 2. 生成Access Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("email", user.getEmail());

        String accessToken = jwtTokenUtil.generateAccessToken(
                user.getId(), user.getUsername(), claims
        );

        // 3. 生成Refresh Token ID
        String refreshTokenId = IdUtil.fastSimpleUUID();

        // 4. 创建Refresh Token信息
        RefreshTokenInfo tokenInfo = RefreshTokenInfo.builder()
                .tokenId(refreshTokenId)
                .userId(user.getId())
                .username(user.getUsername())
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .loginTime(new Date())
                .lastRefreshTime(new Date())
                .refreshCount(0)
                .isValid(true)
                .build();

        // 5. 限制并发登录数量
        boolean canLogin = tokenRedisUtil.checkAndLimitSessions(
                user.getId(), maxSessionsPerUser, refreshTokenId
        );

        if (!canLogin) {
            throw new RuntimeException("登录设备数已达上限");
        }

        // 6. 存储到Redis
        tokenRedisUtil.storeRefreshTokenInfo(refreshTokenId, tokenInfo, refreshExpiration);
        tokenRedisUtil.storeUserAccessToken(user.getId(), accessToken, expiration);

        // 7. 设置HttpOnly Cookie
        Cookie cookie = new Cookie("refresh_token_id", refreshTokenId);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // 生产环境设为true
        cookie.setPath("/api/auth/refresh"); // 注意路径要和拦截器配置一致
        cookie.setMaxAge(refreshExpiration.intValue());
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);

        // 8. 记录登录日志
        log.info("用户登录成功 - ID: {}, 账号: {}, 设备: {}, IP: {}",
                user.getId(), user.getUsername(), deviceInfo, ipAddress);

        // 9. 返回结果
        return LoginResultVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .accessToken(accessToken)
                .expiresIn(expiration)
                .tokenType("Bearer")
                .role(user.getRole())
                .avatar(user.getAvatar())
                .build();
    }

    /**
     * 刷新访问令牌
     */
    public LoginResultVO refreshToken(HttpServletRequest request,
                                      HttpServletResponse response) {

        // 1. 从Cookie获取Refresh Token ID
        String refreshTokenId = extractRefreshTokenIdFromCookie(request);
        if (refreshTokenId == null) {
            log.warn("刷新令牌失败：Cookie中未找到refresh_token_id");
            throw new RuntimeException("未找到刷新令牌");
        }

        // 2. 从Redis获取Refresh Token信息
        RefreshTokenInfo tokenInfo = tokenRedisUtil.getRefreshTokenInfo(refreshTokenId);
        if (tokenInfo == null || !tokenInfo.getIsValid()) {
            log.warn("刷新令牌失败：Refresh Token无效或已过期: {}", refreshTokenId);

            // 清除无效的Cookie
            clearRefreshTokenCookie(response);
            throw new RuntimeException("刷新令牌已过期，请重新登录");
        }

        // 3. 获取用户信息
        LoginResultVO user = userService.selectUserById(tokenInfo.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 4. 检测设备是否变化（安全检查）
        String currentDevice = request.getHeader("User-Agent");
        if (!tokenInfo.getUserAgent().equals(currentDevice)) {
            log.warn("设备变化检测 - 用户: {}, 原设备: {}, 新设备: {}",
                    user.getUsername(), tokenInfo.getUserAgent(), currentDevice);
            // 这里可以发送安全警报
        }

        // 5. 生成新的Access Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("email", user.getEmail());

        String newAccessToken = jwtTokenUtil.generateAccessToken(
                user.getId(), user.getUsername(), claims
        );

        String newRefreshTokenId;

        if (enableTokenRotation) {
            // 令牌轮换策略：生成新的Refresh Token
            newRefreshTokenId = IdUtil.fastSimpleUUID();

            // 更新Token信息
            tokenInfo.setTokenId(newRefreshTokenId);
            tokenInfo.setRefreshCount(tokenInfo.getRefreshCount() + 1);
            tokenInfo.setLastRefreshTime(new Date());
            tokenInfo.setIpAddress(getClientIp(request));
            tokenInfo.setUserAgent(currentDevice);

            // 存储新的Refresh Token
            tokenRedisUtil.storeRefreshTokenInfo(newRefreshTokenId, tokenInfo, refreshExpiration);

            // 更新会话记录
            tokenRedisUtil.removeUserSession(user.getId(), refreshTokenId);
            tokenRedisUtil.addUserSession(user.getId(), newRefreshTokenId);

            // 删除旧的Refresh Token
            tokenRedisUtil.deleteRefreshTokenInfo(refreshTokenId);
        } else {
            // 不轮换，只更新最后刷新时间
            newRefreshTokenId = refreshTokenId;
            tokenInfo.setLastRefreshTime(new Date());
            tokenInfo.setRefreshCount(tokenInfo.getRefreshCount() + 1);
            tokenInfo.setIpAddress(getClientIp(request));
            tokenInfo.setUserAgent(currentDevice);
            tokenRedisUtil.storeRefreshTokenInfo(refreshTokenId, tokenInfo, refreshExpiration);
        }

        // 6. 更新用户访问令牌
        tokenRedisUtil.storeUserAccessToken(user.getId(), newAccessToken, expiration);

        // 7. 更新Cookie
        Cookie cookie = new Cookie("refresh_token_id", newRefreshTokenId);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(refreshExpiration.intValue());
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);

        log.info("令牌刷新成功 - 用户: {}, 刷新次数: {}",
                user.getUsername(), tokenInfo.getRefreshCount());

        // 8. 返回新的Access Token
        return LoginResultVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .accessToken(newAccessToken)
                .expiresIn(expiration)
                .tokenType("Bearer")
                .role(user.getRole())
                .avatar(user.getAvatar())
                .build();
    }

    /**
     * 用户登出
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        // 1. 从Cookie获取Refresh Token ID
        String refreshTokenId = extractRefreshTokenIdFromCookie(request);

        if (refreshTokenId != null) {
            // 2. 获取Token信息
            RefreshTokenInfo tokenInfo = tokenRedisUtil.getRefreshTokenInfo(refreshTokenId);

            if (tokenInfo != null) {
                Long userId = tokenInfo.getUserId();

                // 3. 获取当前Access Token
                String accessToken = tokenRedisUtil.getUserAccessToken(userId);

                // 4. 将Access Token加入黑名单
                if (accessToken != null) {
                    tokenRedisUtil.addToBlacklist(accessToken, expiration);
                }

                // 5. 删除Refresh Token
                tokenRedisUtil.deleteRefreshTokenInfo(refreshTokenId);

                // 6. 删除会话记录
                tokenRedisUtil.removeUserSession(userId, refreshTokenId);

                log.info("用户登出 - userId: {}, refreshTokenId: {}", userId, refreshTokenId);
            }
        }

        // 7. 清除Cookie
        clearRefreshTokenCookie(response);
    }

    /**
     * 验证访问令牌
     */
    public Long validateAccessToken(String token) {

        // 1. 检查是否在黑名单中
        if (tokenRedisUtil.isTokenBlacklisted(token)) {
            log.warn("令牌已被加入黑名单");
            return null;
        }

        // 2. 验证JWT
        if (!jwtTokenUtil.validateToken(token)) {
            return null;
        }

        // 3. 从JWT获取用户ID
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        if (userId == null) {
            return null;
        }

        // 4. 验证令牌是否与Redis中存储的一致（单设备登录）
        String storedToken = tokenRedisUtil.getUserAccessToken(userId);
        if (storedToken == null || !storedToken.equals(token)) {
            log.warn("令牌不匹配，可能在其他设备登录: userId={}", userId);

            // 将当前令牌加入黑名单
            long expireTime = jwtTokenUtil.getRemainingExpireTime(token);
            if (expireTime > 0) {
                tokenRedisUtil.addToBlacklist(token, expireTime);
            }
            return null;
        }

        return userId;
    }

    /**
     * 从Cookie中提取Refresh Token ID
     */
    private String extractRefreshTokenIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token_id".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 清除Refresh Token Cookie
     */
    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token_id", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}