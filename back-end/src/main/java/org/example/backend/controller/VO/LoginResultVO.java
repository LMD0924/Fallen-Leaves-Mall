package org.example.backend.controller.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/*
 * @Author:总会落叶
 * @Date:2026/2/6
 * @Description:
 */
@Data
@Builder
public class LoginResultVO implements Serializable {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "张三")
    private String username;

    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "刷新令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "过期时间（秒）", example = "7200")
    private Long expiresIn;

    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType;

    @Schema(description = "用户角色", example = "admin")
    private String role;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;
}
