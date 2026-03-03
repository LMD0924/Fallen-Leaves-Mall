package org.example.commonbackend.util;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/*
 * @Author:总会落叶
 * @Date:2026/3/3
 * @Description: 日志工具类
 */
public class LogUtils {
    /**
     * 生成全局唯一LogUUID
     */
    public static String generateLogUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取客户端真实IP（支持IPv6、反向代理）
     */
    public static String getRealIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "";
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getHeader("X-Real-IP");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多IP情况（X-Forwarded-For可能返回多个IP，取第一个）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 解析UserAgent，获取设备类型
     */
    public static String parseDeviceType(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return "PC";
        }
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("android")) {
            return "Android";
        } else if (userAgent.contains("ios") || userAgent.contains("iphone") || userAgent.contains("ipad")) {
            return "iOS";
        } else if (userAgent.contains("micromessenger") && userAgent.contains("miniProgram")) {
            return "小程序";
        } else if (userAgent.contains("mobile")) {
            return "H5";
        } else {
            return "PC";
        }
    }

    /**
     * 解析UserAgent，获取APP版本（示例逻辑，需根据实际UA格式调整）
     */
    public static String parseAppVersion(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return "";
        }
        // 示例：UA格式如 "MallApp/2.3.5 (Android; 13)"
        if (userAgent.contains("MallApp/")) {
            String[] parts = userAgent.split("MallApp/");
            if (parts.length > 1) {
                return parts[1].split(" ")[0];
            }
        }
        return "";
    }

    /**
     * 获取分布式链路TraceID（示例：从请求头/ThreadLocal获取）
     */
    public static String getTraceId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String traceId = request.getHeader("X-Trace-ID");
            if (StringUtils.isNotBlank(traceId)) {
                return traceId;
            }
        }
        // 没有则生成临时TraceID
        return "TRACE-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
