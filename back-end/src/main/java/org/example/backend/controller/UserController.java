package org.example.backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.backend.common.RestBean;
import org.example.backend.controller.param.UserLoginParam;
import org.springframework.web.bind.annotation.*;

/*
 * @Author:总会落叶
 * @Date:2026/2/5
 * @Description: 用户
 */

@RestController
@RequestMapping("api/user")
@Tag(name = "用户", description = "用户相关接口")
public class UserController {
    @PostMapping("/login")
    public RestBean<String> login(@Valid @RequestBody UserLoginParam userLoginParam) {
        if("admin".equals(userLoginParam.getUsername()) && "admin".equals(userLoginParam.getPassword())){
            return RestBean.success("登录成功");
        }
        return RestBean.failure("用户名或密码错误");
    }
}
