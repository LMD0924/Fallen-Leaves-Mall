package org.example.backend.common;

import lombok.Getter;

/*
 * @Author:总会落叶
 * @Date:2026/2/5
 * @Description: 状态响应码枚举
 */
@Getter
public enum ResultCode {

    // 成功状态码
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    // 服务器错误 5xx
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂时不可用"),

    // 业务错误 1000-1999
    BUSINESS_ERROR(1000, "业务处理失败"),
    DATA_NOT_EXIST(1001, "数据不存在"),
    DATA_ALREADY_EXISTS(1002, "数据已存在"),
    DATA_VALIDATION_FAILED(1003, "数据验证失败"),

    // 用户相关 2000-2999
    USER_NOT_EXIST(2001, "用户不存在"),
    USER_DISABLED(2002, "用户已被禁用"),
    USER_CREDENTIALS_ERROR(2003, "用户名或密码错误"),
    USER_NOT_LOGIN(2004, "用户未登录"),
    USER_TOKEN_EXPIRED(2005, "登录令牌已过期"),
    USER_TOKEN_INVALID(2006, "登录令牌无效"),

    // 权限相关 3000-3999
    PERMISSION_DENIED(3001, "权限不足"),
    ROLE_NOT_EXIST(3002, "角色不存在"),

    // 文件相关 4000-4999
    FILE_UPLOAD_FAILED(4001, "文件上传失败"),
    FILE_TYPE_NOT_ALLOWED(4002, "文件类型不允许"),
    FILE_SIZE_EXCEEDED(4003, "文件大小超出限制"),

    // 第三方服务 5000-5999
    THIRD_PARTY_SERVICE_ERROR(5001, "第三方服务异常"),

    // 系统配置 6000-6999
    CONFIG_ERROR(6001, "系统配置错误");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    // 根据code获取枚举
    public static ResultCode getByCode(Integer code) {
        for (ResultCode resultCode : ResultCode.values()) {
            if (resultCode.getCode().equals(code)) {
                return resultCode;
            }
        }
        return INTERNAL_SERVER_ERROR;
    }
}