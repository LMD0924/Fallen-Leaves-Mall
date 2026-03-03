package org.example.commonbackend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.commonbackend.code.OperatorType;
import org.example.commonbackend.util.LogContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class LogContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        // 自动从 request 中获取用户信息（假设你的认证拦截器已经设置了）
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        String role = (String) request.getAttribute("role");

        if (userId != null) {
            LogContext.set("operatorId", userId.toString());
            LogContext.set("operatorName", username);

            // 根据角色自动设置操作者类型
            if ("管理员".equals(role)) {
                LogContext.set("operatorType", OperatorType.ADMIN);
            } else if ("商家".equals(role)) {
                LogContext.set("operatorType", OperatorType.MERCHANT);
            } else {
                LogContext.set("operatorType", OperatorType.USER);
            }
        }

        // 设置traceId用于分布式追踪
        LogContext.set("traceId", UUID.randomUUID().toString());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        // 请求结束后自动清理，防止内存泄漏
        LogContext.clear();
    }
}