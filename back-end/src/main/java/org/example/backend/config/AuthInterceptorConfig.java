package org.example.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.util.AuthServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * @Author:总会落叶
 * @Date:2026/2/6
 * @Description: 权限拦截器配置
 */
@Slf4j
@Configuration
public class AuthInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private AuthServiceUtil authServiceUtil;

    // 不需要认证的路径
    private static final String[] EXCLUDE_PATHS = {
            "/api/user/login",
            "/api/user/register",
            "/api/user/refresh",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/webjars/**",
            "/doc.html",
            "/error"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
                    @Override
                    public boolean preHandle(HttpServletRequest request,
                                             HttpServletResponse response,
                                             Object handler) throws Exception {

                        // 获取Token
                        String token = extractToken(request);
                        if (token == null) {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"code\":401,\"message\":\"未提供访问令牌\"}");
                            return false;
                        }

                        // 验证Token
                        Long userId = authServiceUtil.validateAccessToken(token);
                        if (userId == null) {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"code\":401,\"message\":\"访问令牌无效或已过期\"}");
                            return false;
                        }

                        // 将用户ID存入请求上下文
                        request.setAttribute("userId", userId);

                        return true;
                    }
                })
                .addPathPatterns("/api/**")
                .excludePathPatterns(EXCLUDE_PATHS);
    }

    /**
     * 从请求中提取Token
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 也可以从参数中获取
        String tokenParam = request.getParameter("token");
        if (tokenParam != null && !tokenParam.isEmpty()) {
            return tokenParam;
        }

        return null;
    }
}

// 请求上下文工具类
@Component
class RequestContext {
    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    public static void setCurrentUserId(Long userId) {
        currentUserId.set(userId);
    }

    public static Long getCurrentUserId() {
        return currentUserId.get();
    }

    public static void clear() {
        currentUserId.remove();
    }
}
