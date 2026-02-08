package org.example.backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 * @Author:总会落叶
 * @Date:2026/2/6
 * @Description: JWT工具类
 */
@Slf4j
@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.issuer}")
    private String issuer;

    // 生成密钥
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(Long userId, String account, Map<String, Object> claims) {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        // 构建JWT
        return Jwts.builder()
                .setHeader(header)
                .setClaims(claims != null ? claims : new HashMap<>())
                .setSubject(userId.toString())
                .claim("account", account)
                .claim("type", "access")
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000 * 2);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("type", "refresh")
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从令牌中获取用户名
     */
    public String getAccountFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("account", String.class);
    }

    /**
     * 获取令牌类型
     */
    public String getTokenType(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("type", String.class);
    }

    /**
     * 获取过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 获取所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT令牌已过期: {}", e.getMessage());
            throw new TokenExpiredException("令牌已过期");
        } catch (MalformedJwtException e) {
            log.warn("JWT令牌格式错误: {}", e.getMessage());
            throw new InvalidTokenException("令牌格式错误");
        } catch (JwtException e) {
            log.warn("JWT令牌验证失败: {}", e.getMessage());
            throw new InvalidTokenException("令牌验证失败");
        }
    }

    /**
     * 验证令牌是否有效
     */
    public boolean validateToken(String token) {
        try {
            getAllClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断令牌是否即将过期（用于刷新）
     */
    public boolean isTokenAboutToExpire(String token) {
        Date expiration = getExpirationDateFromToken(token);
        Date now = new Date();
        // 如果剩余时间小于5分钟，认为即将过期
        return expiration.getTime() - now.getTime() < 5 * 60 * 1000;
    }
}

// 自定义异常
class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}

class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
