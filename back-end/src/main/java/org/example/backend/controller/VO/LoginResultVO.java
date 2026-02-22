package org.example.backend.controller.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author:总会落叶
 * @Date:2026/2/6
 * @Description: 登录返回结果VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResultVO {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "张三")
    private String username;

    @Schema(description = "账号", example = "zhangsan")
    private String account;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "用户角色", example = "管理员")
    private String role;

    @Schema(description = "用户状态", example = "1")
    private Integer status;

    @Schema(description = "是否锁定", example = "0")
    private Integer locked;

    @Schema(description = "创建时间", example = "2023-01-01 00:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2023-01-01 00:00:00")
    private LocalDateTime updateTime;

    // 以下字段仅在登录时使用
    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "刷新令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "过期时间（秒）", example = "7200")
    private Long expiresIn;

    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType;
}