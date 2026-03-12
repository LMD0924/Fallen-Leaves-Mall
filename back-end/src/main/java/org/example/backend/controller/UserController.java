package org.example.backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.common.RestBean;
import org.example.backend.controller.VO.LoginResultVO;
import org.example.backend.entity.User;
import org.example.backend.service.UserService;
import org.example.backend.util.AuthServiceUtil;
import org.example.commonbackend.annotation.OperationLog;
import org.example.commonbackend.code.LogType;
import org.example.commonbackend.code.OperatorType;
import org.example.commonbackend.util.LogContext;
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
    * 根据id查询用户信息(获取自己的信息)
    * */
    @GetMapping("/selectUserById")
    public RestBean<LoginResultVO> selectUserById(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        if(userId == null) return RestBean.failure("身份不合法");
        return RestBean.success(userService.selectUserById(userId));
    }

    /**
     * 返回当前用户id
     * @param
     * @param request
     * @return
     */
    @GetMapping("/getUserId")
    public Long getUserId(HttpServletRequest request){
        log.info("请求头:{}",request.getHeader("Authorization"));
        return (Long) request.getAttribute("userId");
    }

    /*
    * 根据id查询指定用户信息（管理员专用）
    * */
    @GetMapping("/selectUserById/{id}")
    public RestBean<LoginResultVO> selectUserById(@PathVariable("id") Long id, HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        if(userId == null) return RestBean.failure("身份不合法");
        // 获取当前用户信息
        LoginResultVO currentUser = userService.selectUserById(userId);
        // 检查是否为管理员
        if(!"管理员".equals(currentUser.getRole())) {
            return RestBean.failure("权限不足，只有管理员可以查询用户信息");
        }
        return RestBean.success(userService.selectUserById(id));
    }

    /*
    * 更新用户信息
    * */
    @OperationLog(
            logType = LogType.SYSTEM_OPERATION,
            operatorType = OperatorType.ADMIN,
            businessModule = "backend",
            operationAction = "updateUser",
            description = "管理员 ${#operatorName} 修改用户信息，用户ID：${#user.id}",
            businessIdSpel = "#user.id",
            recordOldData = true
    )
    @PostMapping("/adminUpdateUser")
    public RestBean<LoginResultVO> updateUser(@RequestBody User user,
                                              HttpServletRequest request){
        Long adminId = (Long) request.getAttribute("userId");
        if(adminId == null) return RestBean.failure("身份不合法");

        LoginResultVO admin = userService.selectUserById(adminId);

        // 只验证是否是管理员，不验证ID是否相等
        if(!Objects.equals(admin.getRole(), "管理员")) {
            return RestBean.failure("权限不足，需要管理员权限");
        }

        // 记录旧数据（用于更新操作）
        LoginResultVO oldUser = userService.selectUserById(user.getId());
        LogContext.set("oldData", oldUser);
        
        // 确保operatorName被正确设置
        LogContext.set("operatorName", admin.getUsername());

        // 执行更新
        Integer result = userService.updateUser(user);
        if(result == 0) return RestBean.failure("更新失败");

        return RestBean.success("更新成功", userService.selectUserById(user.getId()));
    }

    /*
    * 管理员获取全部用户信息
    * */
    @GetMapping("/selectAllUser")
    public RestBean<List<LoginResultVO>> selectAllUser(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        if(userId!=1) return RestBean.failure("权限不足");
        return RestBean.success(userService.selectAllUser());
    }

    //获取总用户
    @GetMapping("/selectUserCount")
    public RestBean<Long> selectUserCount(){
        return RestBean.success(userService.selectUserCount());
    }

    //获取今日新增用户
    @GetMapping("/selectUserCountByToday")
    public RestBean<Long> selectUserCountByToday(){
        return RestBean.success(userService.selectUserCountByToday());
    }
}
