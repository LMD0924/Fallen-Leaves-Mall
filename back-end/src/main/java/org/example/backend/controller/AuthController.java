package org.example.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.common.RestBean;
import org.example.backend.controller.VO.LoginResultVO;
import org.example.backend.controller.param.UserLoginParam;
import org.example.backend.util.AuthServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 * @Author:总会落叶
 * @Date:2026/2/13
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthServiceUtil authServiceUtil;

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public RestBean<LoginResultVO> login(@RequestBody UserLoginParam userLoginParam,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        try {
            String deviceInfo = request.getHeader("User-Agent");
            String ipAddress = getClientIp(request);

            LoginResultVO result = authServiceUtil.login(
                    userLoginParam.getAccount(),
                    userLoginParam.getPassword(),
                    deviceInfo,
                    ipAddress,
                    deviceInfo,
                    request,
                    response
            );

            return RestBean.success("登录成功", result);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            return RestBean.failure(401, e.getMessage());
        }
    }

    /**
     * 刷新令牌接口
     */
    @PostMapping("/refresh")
    public RestBean<LoginResultVO> refresh(HttpServletRequest request,
                                           HttpServletResponse response) {
        try {
            LoginResultVO result = authServiceUtil.refreshToken(request, response);
            return RestBean.success(result);
        } catch (Exception e) {
            log.error("刷新令牌失败: {}", e.getMessage());
            return RestBean.failure(401, e.getMessage());
        }
    }

    /**
     * 登出接口
     */
    @PostMapping("/logout")
    public RestBean<Void> logout(HttpServletRequest request,
                                 HttpServletResponse response) {
        authServiceUtil.logout(request, response);
        return RestBean.success();
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
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
