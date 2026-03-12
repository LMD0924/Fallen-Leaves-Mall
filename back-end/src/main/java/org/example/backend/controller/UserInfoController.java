package org.example.backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backend.common.RestBean;
import org.example.backend.entity.UserInfo;
import org.example.backend.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 * @Author:总会落叶
 * @Date:2026/3/11
 * @Description:用户信息控制器
 */
@RestController
@RequestMapping("api/user/info")
@Tag(name = "用户信息", description = "用户信息相关接口")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /*
     * 获取用户信息
     * */
    @GetMapping("/getUserInfo")
    public RestBean<UserInfo> getUserInfo(@RequestParam("userId") Long userId) {
        if (userId == null) {
            return RestBean.failure("用户ID不能为空");
        }
        UserInfo userInfo = userInfoService.getUserInfoByUserId(userId);
        if (userInfo == null) {
            // 初始化用户信息
            userInfoService.initUserInfo(userId);
            userInfo = userInfoService.getUserInfoByUserId(userId);
        }
        return RestBean.success(userInfo);
    }

    /*
     * 初始化用户信息
     * */
    @PostMapping("/initUserInfo")
    public RestBean<String> initUserInfo(@RequestParam("userId") Long userId) {
        if (userId == null) {
            return RestBean.failure("用户ID不能为空");
        }
        userInfoService.initUserInfo(userId);
        return RestBean.success("初始化成功");
    }

    /*
     * 更新用户余额
     * */
    @PostMapping("/updateBalance")
    public RestBean<String> updateBalance(@RequestParam("userId") Long userId, @RequestParam("amount") Double amount) {
        if (userId == null || amount == null) {
            return RestBean.failure("参数不能为空");
        }
        boolean result = userInfoService.updateBalance(userId, amount);
        if (result) {
            return RestBean.success("余额更新成功");
        } else {
            return RestBean.failure("余额更新失败");
        }
    }

    /*
     * 更新用户积分
     * */
    @PostMapping("/updatePoints")
    public RestBean<String> updatePoints(@RequestParam("userId") Long userId, @RequestParam("points") Long points) {
        if (userId == null || points == null) {
            return RestBean.failure("参数不能为空");
        }
        boolean result = userInfoService.updatePoints(userId, points);
        if (result) {
            return RestBean.success("积分更新成功");
        } else {
            return RestBean.failure("积分更新失败");
        }
    }

    /*
     * 更新会员等级
     * */
    @PostMapping("/updateVipLevel")
    public RestBean<String> updateVipLevel(@RequestParam("userId") Long userId, @RequestParam("vipLevel") Integer vipLevel, @RequestParam(value = "expireTime", required = false) String expireTime) {
        if (userId == null || vipLevel == null) {
            return RestBean.failure("参数不能为空");
        }
        boolean result = userInfoService.updateVipLevel(userId, vipLevel, expireTime);
        if (result) {
            return RestBean.success("会员等级更新成功");
        } else {
            return RestBean.failure("会员等级更新失败");
        }
    }
}
