package org.example.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.util.AuthServiceUtil;
import org.example.backend.util.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",  // 刷新接口放行，但内部会验证Cookie
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/webjars/**",
            "/doc.html",
            "/error",
            "/api/public/**"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
                    @Override
                    public boolean preHandle(HttpServletRequest request,
                                             HttpServletResponse response,
                                             Object handler) throws Exception {

                        // 设置响应编码
                        response.setCharacterEncoding("UTF-8");
                        response.setContentType("application/json;charset=UTF-8");

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
                        RequestContext.setCurrentUserId(userId);
                        request.setAttribute("userId", userId);

                        return true;
                    }

                    @Override
                    public void afterCompletion(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Object handler,
                                                Exception ex) throws Exception {
                        // 清理ThreadLocal，防止内存泄漏
                        RequestContext.clear();
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
