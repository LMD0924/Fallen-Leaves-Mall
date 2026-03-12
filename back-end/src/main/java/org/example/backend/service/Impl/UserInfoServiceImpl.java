package org.example.backend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.backend.entity.UserInfo;
import org.example.backend.mapper.UserInfoMapper;
import org.example.backend.service.UserInfoService;
import org.example.commonbackend.code.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/*
 * @Author:总会落叶
 * @Date:2026/3/11
 * @Description:用户信息Service实现
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo getUserInfoByUserId(Long userId) {
        return userInfoMapper.selectByUserId(userId);
    }

    @Override
    public void initUserInfo(Long userId) {
        // 检查用户信息是否已存在
        UserInfo existingUser = userInfoMapper.selectByUserId(userId);
        if (existingUser == null) {
            // 初始化用户信息
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(userId);
            userInfo.setBalance(0.0);
            userInfo.setPoints(0L);
            userInfo.setVipLevel(UserStatus.NORMAL);
            userInfo.setVipExpireTime(null);
            userInfoMapper.insert(userInfo);
        }
    }

    @Override
    public boolean updateBalance(Long userId, Double amount) {
        UserInfo userInfo = userInfoMapper.selectByUserId(userId);
        if (userInfo != null) {
            double newBalance = userInfo.getBalance() + amount;
            if (newBalance < 0) {
                return false; // 余额不足
            }
            userInfo.setBalance(newBalance);
            return userInfoMapper.updateById(userInfo) > 0;
        }
        return false;
    }

    @Override
    public boolean updatePoints(Long userId, Long points) {
        UserInfo userInfo = userInfoMapper.selectByUserId(userId);
        if (userInfo != null) {
            long newPoints = userInfo.getPoints() + points;
            if (newPoints < 0) {
                return false; // 积分不足
            }
            userInfo.setPoints(newPoints);
            return userInfoMapper.updateById(userInfo) > 0;
        }
        return false;
    }

    @Override
    public boolean updateVipLevel(Long userId, Integer vipLevel, String expireTime) {
        UserInfo userInfo = userInfoMapper.selectByUserId(userId);
        if (userInfo != null) {
            userInfo.setVipLevel(UserStatus.getByCode(vipLevel));
            if (expireTime != null) {
                userInfo.setVipExpireTime(LocalDateTime.parse(expireTime));
            } else {
                userInfo.setVipExpireTime(null);
            }
            return userInfoMapper.updateById(userInfo) > 0;
        }
        return false;
    }
}
