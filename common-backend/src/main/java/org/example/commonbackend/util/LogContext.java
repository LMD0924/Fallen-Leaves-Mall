package org.example.commonbackend.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import java.util.HashMap;
import java.util.Map;

/*
 * @Author:总会落叶
 * @Date:2026/3/3
 * @Description:
 */
public class LogContext {
    // 核心容器：TransmittableThreadLocal（简称TTL），存储键值对形式的上下文数据
    // 相比普通ThreadLocal，TTL能解决线程池中子线程无法继承父线程ThreadLocal的问题
    private static final TransmittableThreadLocal<Map<String, Object>> CONTEXT = new TransmittableThreadLocal<>();

    /**
     * 设置上下文变量（比如存用户ID、TraceID、租户ID）
     */
    public static void set(String key, Object value) {
        // 先获取当前线程的上下文Map
        Map<String, Object> map = CONTEXT.get();
        // 如果Map为空，初始化一个新的HashMap并放入TTL
        if (map == null) {
            map = new HashMap<>();
            CONTEXT.set(map);
        }
        // 往Map中存入键值对（比如key="operatorId", value="U10001"）
        map.put(key, value);
    }

    /**
     * 获取上下文变量（比如取之前存的用户ID）
     */
    public static Object get(String key) {
        Map<String, Object> map = CONTEXT.get();
        // 若Map为空返回null，否则根据key取值
        return map == null ? null : map.get(key);
    }

    /**
     * 获取所有上下文（比如一次性获取用户ID、TraceID、租户ID）
     */
    public static Map<String, Object> getAll() {
        Map<String, Object> map = CONTEXT.get();
        // 防止返回null，空的话返回空HashMap
        return map == null ? new HashMap<>() : map;
    }

    /**
     * 清理上下文（线程执行完后调用，防止内存泄漏）
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
