package org.example.backend.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/*
 * @Author:总会落叶
 * @Date:2026/2/5
 * @Description: REST API 响应封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestBean<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;    // 状态码
    private String message;  // 提示信息
    private T data;         // 响应数据
    private Long timestamp; // 时间戳

    // 构造函数
    public RestBean(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // 使用枚举状态码
    public RestBean(ResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // 成功响应（无数据）
    public static <T> RestBean<T> success() {
        return new RestBean<>(ResultCode.SUCCESS, null);
    }

    // 成功响应（带数据）
    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(ResultCode.SUCCESS, data);
    }

    // 成功响应（自定义消息）
    public static <T> RestBean<T> success(String message, T data) {
        return new RestBean<>(200, message, data, System.currentTimeMillis());
    }

    // 失败响应（使用枚举）
    public static <T> RestBean<T> failure(ResultCode resultCode) {
        return new RestBean<>(resultCode, null);
    }

    // 失败响应（自定义）
    public static <T> RestBean<T> failure(Integer code, String message) {
        return new RestBean<>(code, message, null);
    }

    // 快速失败响应
    public static <T> RestBean<T> failure(String message) {
        return new RestBean<>(400, message, null);
    }

    // 业务异常响应
    public static <T> RestBean<T> businessError(String message) {
        return new RestBean<>(ResultCode.BUSINESS_ERROR.getCode(), message, null);
    }

    // 判断是否成功
    public Boolean isSuccess() {
        return this.code != null && this.code == 200;
    }

    // 获取数据（安全方式）
    public T getDataSafely() {
        return this.data;
    }

    // 链式调用支持
    public RestBean<T> code(Integer code) {
        this.code = code;
        return this;
    }

    public RestBean<T> message(String message) {
        this.message = message;
        return this;
    }

    public RestBean<T> data(T data) {
        this.data = data;
        return this;
    }
}