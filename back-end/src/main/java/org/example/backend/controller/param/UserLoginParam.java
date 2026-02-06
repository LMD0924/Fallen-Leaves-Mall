package org.example.backend.controller.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/*
 * @Author:总会落叶
 * @Date:2026/2/5
 * @Description:
 */
@Data
@Schema(description = "登录参数")
//实现Serializable接口，以便在序列化和反序列化时能够正确处理对象
public class UserLoginParam implements Serializable {
    @NotBlank(message = "账号不能为空")
    @Schema(description = "账号")
    private String account;
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;
}
