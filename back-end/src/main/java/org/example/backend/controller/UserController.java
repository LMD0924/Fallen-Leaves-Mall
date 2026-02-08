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
import org.example.backend.util.AuthServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
