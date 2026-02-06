package org.example.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.backend.entity.User;

/*
 * @Author:总会落叶
 * @Date:2026/2/6
 * @Description:
 */
@Mapper
public interface UserMapper {
    /*
    * 用户登录
    * */
    User login(String account, String password);

    /*
    * 根据id查询用户
    * */
    User selectUserById(Long id);

    /*
    * 更新用户信息
    * */
    int updateUser(User user);

    /*
    * 根据账号查询用户
    * */
    User selectUserByAccount(String account);
}
