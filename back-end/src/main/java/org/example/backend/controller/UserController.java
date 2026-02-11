package org.example.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public RestBean<LoginResultVO> login(@Valid @RequestBody UserLoginParam request,
                                       HttpServletRequest httpRequest) {

        String deviceInfo = httpRequest.getHeader("User-Agent");
        String ipAddress = getClientIp(httpRequest);

        LoginResultVO result = authServiceUtil.login(
                request.getAccount(),
                request.getPassword(),
                deviceInfo,
                ipAddress
        );

        log.info("用户 {} 登录成功，IP: {}", request.getAccount(), ipAddress);
        return RestBean.success("登录成功", result);
    }

    /*
    * 根据id查询用户信息
    * */
    @GetMapping("/selectUserById")
    public RestBean<LoginResultVO> selectUserById(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("id");
        if(userId == null) return RestBean.failure("身份不合法");
        return RestBean.success(userService.selectUserById(userId));
    }

    /*
    * 更新用户信息
    * */
    @PostMapping("/updateUser")
    public RestBean<LoginResultVO> updateUser(@RequestBody User user,
                                              HttpServletRequest request){
        Long userId = (Long) request.getAttribute("id");
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
        Long userId = (Long) request.getAttribute("id");
        if(userId!=1) return RestBean.failure("权限不足");
        return RestBean.success(userService.selectAllUser());
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public RestBean<Void> logout(@RequestHeader("Authorization") String token,
                               @RequestAttribute Long userId) {
        authServiceUtil.logout(token.replace("Bearer ", ""), userId);
        return RestBean.success();
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新访问令牌")
    public RestBean<LoginResultVO> refreshToken(@RequestBody RefreshTokenRequest request,
                                              HttpServletRequest httpRequest) {

        LoginResultVO result = authServiceUtil.refreshToken(request.getRefreshToken(), httpRequest);
        return RestBean.success(result);
    }

    @GetMapping("/validate")
    @Operation(summary = "验证令牌")
    public RestBean<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        Long userId = authServiceUtil.validateAccessToken(token.replace("Bearer ", ""));
        return RestBean.success(userId != null);
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
