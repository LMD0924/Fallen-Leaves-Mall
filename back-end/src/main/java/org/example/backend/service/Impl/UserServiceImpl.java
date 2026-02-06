package org.example.backend.service.Impl;

import org.example.backend.common.RestBean;
import org.example.backend.entity.User;
import org.example.backend.mapper.UserMapper;
import org.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/*
 * @Author:总会落叶
 * @Date:2026/2/6
 * @Description:
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String account, String password) {
        User user = userMapper.selectUserByAccount(account);
        if(user==null) throw new RuntimeException("用户不存在");
        if(!Objects.equals(user.getLocked(), "0")) throw new RuntimeException("用户已被锁定");
        return userMapper.login(account, password);
    }

    @Override
    public User selectUserById(Long id) {
        return userMapper.selectUserById(id);
    }

    @Override
    public RestBean<String> updateUser(User user) {
        int result = userMapper.updateUser(user);
        if(result<0) return RestBean.failure("更新失败");
        return RestBean.success("更新成功");
    }
}
