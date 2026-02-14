package org.example.backend.util;

import org.springframework.stereotype.Component;

/*
 * @Author:总会落叶
 * @Date:2026/2/13
 * @Description: 请求上下文 - 用于存储当前线程的用户信息
 */
@Component
public class RequestContext {
    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentToken = new ThreadLocal<>();

    public static void setCurrentUserId(Long userId) {
        currentUserId.set(userId);
    }

    public static Long getCurrentUserId() {
        return currentUserId.get();
    }

    public static void setCurrentToken(String token) {
        currentToken.set(token);
    }

    public static String getCurrentToken() {
        return currentToken.get();
    }

    public static void clear() {
        currentUserId.remove();
        currentToken.remove();
    }
}
