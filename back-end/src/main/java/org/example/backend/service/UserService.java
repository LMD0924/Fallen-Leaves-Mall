package org.example.backend.service;

/*
 * @Author:总会落叶
 * @Date:2026/2/6
 * @Description:
 */

import org.example.backend.common.RestBean;
import org.example.backend.controller.VO.LoginResultVO;
import org.example.backend.entity.User;

import java.util.List;

public interface UserService {
    //用户登录
    User login(String account, String password);
    //根据id查询用户
    LoginResultVO selectUserById(Long id);
    //管理员获取全部用户
    List<LoginResultVO> selectAllUser();
    //修改用户信息
    Integer updateUser(User user);
}
