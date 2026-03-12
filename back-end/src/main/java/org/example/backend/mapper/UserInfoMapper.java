package org.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.backend.entity.UserInfo;

/*
 * @Author:总会落叶
 * @Date:2026/3/11
 * @Description:用户信息Mapper
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    // 根据用户ID查询用户信息
    UserInfo selectByUserId(Long userId);
}
