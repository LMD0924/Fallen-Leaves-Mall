package org.example.commonbackend.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.commonbackend.annotation.OperationLog;
import org.example.commonbackend.code.OperatorType;
import org.example.commonbackend.entity.MallOperationLog;
import org.example.commonbackend.service.IOperationLogService;
import org.example.commonbackend.util.JsonUtil;
import org.example.commonbackend.util.LogContext;
import org.example.commonbackend.util.SpelExpressionParserUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.UUID;

/*
 * @Author:总会落叶
 * @Date:2026/3/2
 * @Description:
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MallOperationLogAspect  {

    private final IOperationLogService operationLogService;
    private final SpelExpressionParserUtil spelParser;

    /*
    * 切点：所有加了@MallOperationLog注解的方法
    * */
    @Pointcut("@annotation(org.example.commonbackend.annotation.OperationLog)")
    public void logPointcut(){

    }

    /**
     * 环绕通知
     */
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取方法签名和注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        OperationLog operationLog = getOperationLog(signature, joinPoint);

        // 构建日志对象
        MallOperationLog logEntity = buildBaseLog(operationLog, joinPoint);

        // 记录请求参数
        if (operationLog.recordParams()) {
            logEntity.setNewData(JsonUtil.toJson(joinPoint.getArgs()));
        }

        // 记录旧数据（如果有查询旧数据的逻辑，可以在调用方法前通过其他方式设置到LogContext）
        if (operationLog.recordOldData()) {
            Object oldData = LogContext.get("oldData");
            if (oldData != null) {
                logEntity.setOldData(JsonUtil.toJson(oldData));
            }
        }

        Object result = null;
        Throwable error = null;

        try {
            // 执行原方法
            result = joinPoint.proceed();

            // 记录返回结果
            if (operationLog.recordResult()) {
                logEntity.setChangeData(JsonUtil.toJson(result));
            }

            logEntity.setResultStatus(1); // 成功
            return result;

        } catch (Throwable e) {
            error = e;
            logEntity.setResultStatus(2); // 失败
            logEntity.setErrorMsg(e.getMessage());
            logEntity.setErrorCode(getErrorCode(e));
            throw e;

        } finally {
            // 设置执行耗时
            if (operationLog.saveDuration()) {
                logEntity.setExecutionDuration((int) (System.currentTimeMillis() - startTime));
            }

            // 解析SpEL描述
            if (operationLog.description() != null && !operationLog.description().isEmpty()) {
                String desc = spelParser.parseExpression(
                        operationLog.description(),
                        signature.getMethod(),
                        joinPoint.getArgs(),
                        result,
                        error
                );
                logEntity.setOperationDesc(desc);
            }

            // 解析业务ID
            if (operationLog.businessIdSpel() != null && !operationLog.businessIdSpel().isEmpty()) {
                String businessId = spelParser.parseBusinessId(
                        operationLog.businessIdSpel(),
                        signature.getMethod(),
                        joinPoint.getArgs()
                );
                logEntity.setBusinessId(businessId);
            }

            // 解析业务单号
            if (operationLog.businessNoSpel() != null && !operationLog.businessNoSpel().isEmpty()) {
                String businessNo = spelParser.parseBusinessId(
                        operationLog.businessNoSpel(),
                        signature.getMethod(),
                        joinPoint.getArgs()
                );
                logEntity.setBusinessNo(businessNo);
            }

            // 异步保存日志（避免影响主流程）
            try {
                operationLogService.saveAsync(logEntity);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            } finally {
                // 清理上下文
                LogContext.clear();
            }
        }
    }

    /**
     * 获取注解（优先方法上的，其次类上的）
     */
    private OperationLog getOperationLog(MethodSignature signature, ProceedingJoinPoint joinPoint) {
        OperationLog methodAnnotation = signature.getMethod().getAnnotation(OperationLog.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }

        // 获取类上的注解
        Class<?> targetClass = joinPoint.getTarget().getClass();
        return targetClass.getAnnotation(OperationLog.class);
    }

    /**
     * 构建基础日志信息
     */
    private MallOperationLog buildBaseLog(OperationLog operationLog, ProceedingJoinPoint joinPoint) {
        MallOperationLog log = new MallOperationLog();

        // 基本字段
        log.setLogUuid(UUID.randomUUID().toString().replace("-", ""));
        log.setLogType(operationLog.logType());
        log.setBusinessModule(operationLog.businessModule());
        log.setBusinessType(operationLog.businessType());
        log.setOperationAction(operationLog.operationAction());

        // 从LogContext获取操作者信息（需要在调用前设置）
        log.setOperatorId(getStringFromContext("operatorId"));
        log.setOperatorName(getStringFromContext("operatorName"));
        Integer operatorTypeCode = getIntegerFromContext("operatorType");
        if (operatorTypeCode != null) {
            log.setOperatorType(OperatorType.fromCode(operatorTypeCode));
        }
        log.setTenantId(getLongFromContext("tenantId"));

        // 从请求中获取IP、User-Agent等信息
        setRequestInfo(log);

        // 设置TraceId（可用于分布式追踪）
        log.setTraceId(getStringFromContext("traceId"));
        if (log.getTraceId() == null) {
            log.setTraceId(UUID.randomUUID().toString().replace("-", ""));
        }

        log.setCreateTime(LocalDateTime.now());

        return log;
    }

    /**
     * 设置请求相关信息
     */
    private void setRequestInfo(MallOperationLog log1) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                log1.setRequestUrl(request.getRequestURI());
                log1.setRequestMethod(request.getMethod());
                log1.setUserAgent(request.getHeader("User-Agent"));
                log1.setOperatorIp(getClientIp(request));

                // 从请求头获取设备类型
                String deviceType = request.getHeader("Device-Type");
                log1.setDeviceType(deviceType);

                String appVersion = request.getHeader("App-Version");
                log1.setAppVersion(appVersion);
            }
        } catch (Exception e) {
            log.info("获取请求信息失败",e);
        }
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理的情况，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 从上下文中获取字符串值
     */
    private String getStringFromContext(String key) {
        Object value = LogContext.get(key);
        return value != null ? value.toString() : null;
    }

    private Integer getIntegerFromContext(String key) {
        Object value = LogContext.get(key);
        return value instanceof Integer ? (Integer) value : null;
    }

    private Long getLongFromContext(String key) {
        Object value = LogContext.get(key);
        return value instanceof Long ? (Long) value :
                value instanceof Integer ? ((Integer) value).longValue() : null;
    }

    /**
     * 获取错误码（可根据异常类型自定义）
     */
    private String getErrorCode(Throwable e) {
        // 这里可以根据异常类型返回不同的错误码
        if (e instanceof IllegalArgumentException) {
            return "PARAM_ERROR";
        } else if (e instanceof NullPointerException) {
            return "NPE_ERROR";
        } else if (e instanceof RuntimeException) {
            return "BIZ_ERROR";
        }
        return "SYS_ERROR";
    }

}
