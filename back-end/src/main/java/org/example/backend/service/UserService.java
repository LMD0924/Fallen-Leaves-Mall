package org.example.backend.service;

/*
 * @Author:总会落叶
 * @Date:2026/2/6
 * @Description:
 */

import org.example.backend.common.RestBean;
import org.example.backend.entity.User;

public interface UserService {
    //用户登录
    User login(String account, String password);
    //根据id查询用户
    User selectUserById(Long id);
    //修改用户信息
    RestBean<String> updateUser(User user);
}
