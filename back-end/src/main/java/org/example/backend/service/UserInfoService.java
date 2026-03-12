package org.example.backend.service;

import org.example.backend.entity.UserInfo;

/*
 * @Author:总会落叶
 * @Date:2026/3/11
 * @Description:用户信息Service
 */
public interface UserInfoService {
    // 根据用户ID获取用户信息
    UserInfo getUserInfoByUserId(Long userId);

    // 初始化用户信息
    void initUserInfo(Long userId);

    // 更新用户余额
    boolean updateBalance(Long userId, Double amount);

    // 更新用户积分
    boolean updatePoints(Long userId, Long points);

    // 更新会员等级
    boolean updateVipLevel(Long userId, Integer vipLevel, String expireTime);
}
