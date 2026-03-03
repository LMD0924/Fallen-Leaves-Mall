package org.example.commonbackend.util;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;
/*
 * @Author:总会落叶
 * @Date:2026/3/3
 * @Description:
 */
@Component
public class SpelExpressionParserUtil {

    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    /**
     * 解析SpEL表达式
     */
    public String parseExpression(String expression, Method method, Object[] args, Object result, Throwable error) {
        if (!StringUtils.hasText(expression)) {
            return "";
        }

        try {
            // 获取方法参数名
            String[] paramNames = discoverer.getParameterNames(method);

            // 创建评估上下文
            StandardEvaluationContext context = new MethodBasedEvaluationContext(null, method, args, discoverer);

            // 设置根对象
            context.setRootObject(args);

            // 设置参数变量
            if (paramNames != null) {
                for (int i = 0; i < paramNames.length; i++) {
                    context.setVariable(paramNames[i], args[i]);
                }
            }

            // 设置返回结果和异常
            context.setVariable("result", result);
            context.setVariable("error", error);

            // 设置上下文变量
            Map<String, Object> contextVars = LogContext.getAll();
            contextVars.forEach((key, value) -> {
                if (value != null) {
                    context.setVariable(key, value);
                }
            });

            return parser.parseExpression(expression).getValue(context, String.class);
        } catch (Exception e) {
            // 解析失败时返回原始表达式，并记录日志
            return expression + " (解析失败: " + e.getMessage() + ")";
        }
    }

    /**
     * 解析业务ID
     */
    public String parseBusinessId(String expression, Method method, Object[] args) {
        if (!StringUtils.hasText(expression)) {
            return null;
        }
        return parseExpression(expression, method, args, null, null);
    }
}