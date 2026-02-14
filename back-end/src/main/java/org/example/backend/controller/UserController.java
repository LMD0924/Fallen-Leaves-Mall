package org.example.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.common.RestBean;
import org.example.backend.controller.VO.LoginResultVO;
import org.example.backend.controller.param.UserLoginParam;
import org.example.backend.entity.User;
import org.example.backend.service.UserService;
import org.example.backend.util.AuthServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/*
 * @Author:总会落叶
 * @Date:2026/2/5
 * @Description: 用户
 */
@Slf4j
@RestController
@RequestMapping("api/user")
@Tag(name = "用户", description = "用户相关接口")
public class UserController {

    @Data
    static class RefreshTokenRequest {
        @NotBlank(message = "刷新令牌不能为空")
        private String refreshToken;
    }

    @Autowired
    private AuthServiceUtil authServiceUtil;
    @Autowired
    private UserService userService;

    /*
    * 根据id查询用户信息
    * */
    @GetMapping("/selectUserById")
    public RestBean<LoginResultVO> selectUserById(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        if(userId == null) return RestBean.failure("身份不合法");
        return RestBean.success(userService.selectUserById(userId));
    }

    /*
    * 更新用户信息
    * */
    @PostMapping("/updateUser")
    public RestBean<LoginResultVO> updateUser(@RequestBody User user,
                                              HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        if(userId == null) return RestBean.failure("身份不合法");
        LoginResultVO result = userService.selectUserById(userId);
        if(!Objects.equals(result.getRole(),"管理员") || !userId.equals(user.getId())) return RestBean.failure("权限不足");
        Integer result1 = userService.updateUser(user);
        if(result1 == 0) return RestBean.failure("更新失败");
        return RestBean.success("更新成功",userService.selectUserById(user.getId()));
    }

    /*
    * 管理员获取全部用户信息
    * */
    @GetMapping("/selectAllUser")
    public RestBean<List<LoginResultVO>> selectAllUser(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId" +
                "");
        if(userId!=1) return RestBean.failure("权限不足");
        return RestBean.success(userService.selectAllUser());
    }
}
