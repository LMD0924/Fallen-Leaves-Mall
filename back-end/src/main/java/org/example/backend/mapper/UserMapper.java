package org.example.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.backend.controller.VO.LoginResultVO;
import org.example.backend.entity.User;

import java.util.List;

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
    LoginResultVO selectUserById(Long id);

    /*
    * 管理员查询全部用户
    * */
    List<LoginResultVO> selectAllUser();

    /*
    * 更新用户信息
    * */
    int updateUser(User user);

    /*
    * 根据账号查询用户
    * */
    User selectUserByAccount(String account);
}
