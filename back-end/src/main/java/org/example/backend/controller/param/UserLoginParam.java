package org.example.backend.controller.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/*
 * @Author:总会落叶
 * @Date:2026/2/5
 * @Description:
 */
@Data
@Schema(description = "登录参数")
public class UserLoginParam {
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;
}
