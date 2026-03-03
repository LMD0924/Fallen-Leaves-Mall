package org.example.backend.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.backend.common.RestBean;
import org.example.backend.controller.VO.LoginResultVO;
import org.example.backend.entity.User;
import org.example.backend.mapper.UserMapper;
import org.example.backend.service.UserService;
import org.example.commonbackend.annotation.OperationLog;
import org.example.commonbackend.code.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * @Author:总会落叶
 * @Date:2026/2/6
 * @Description:
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String account, String password) {
        // 1. 先根据账号查询用户
        User user = userMapper.selectUserByAccount(account);

        // 2. 验证用户是否存在
        if(user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 3. 验证账号是否被锁定
        if(user.getLocked() != UserStatus.NORMAL) {
            throw new RuntimeException("用户已被锁定");
        }

        // 4. 验证密码（这里应该有密码加密验证）
        // 假设 userMapper.login 会验证密码并返回用户信息
        User loginUser = userMapper.login(account, password);

        if(loginUser == null) {
            throw new RuntimeException("密码错误");
        }

        // 5. 清除敏感信息
        loginUser.setPassword(null);  // 移除密码

        return loginUser;
    }

    @Override
    public LoginResultVO selectUserById(Long id) {
        return userMapper.selectUserById(id);
    }

    /*
    * 管理员查询全部用户
    * */
    @Override
    public List<LoginResultVO> selectAllUser() {
        return userMapper.selectAllUser();
    }

    @Override
    public Integer updateUser(User user) {
        int result = userMapper.updateUser(user);
        if(result<0) throw new RuntimeException("更新失败");
        return result;
    }

    //获取用户总数
    @Override
    public Long selectUserCount(){
        return lambdaQuery()
                .eq(User::getLocked, UserStatus.NORMAL)
                .count();
    }

    //今日新增用户
    @Override
    public Long selectUserCountByToday(){
        return lambdaQuery()
                .eq(User::getLocked,UserStatus.NORMAL)
                .ge(User::getCreateTime,RestBean.getTodayStartTime())
                .le(User::getCreateTime,RestBean.getTodayEndTime())
                .count();
    }

}
