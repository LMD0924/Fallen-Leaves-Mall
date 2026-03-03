package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.commonbackend.code.UserStatus;

import java.time.LocalDateTime;

/**
 * @Author:总会落叶
 * @Date:2026/2/6
 * @Description: 用户实体类（根据数据库表结构调整）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {
    private Long id;              // bigint - 主键ID
    private String account;       // varchar(50) - 登录名（唯一）
    private String password;      // varchar(50) - 登录密码
    private String username;      // varchar(50) - 昵称
    private UserStatus locked;       // tinyint - 是否锁定（0-未锁定，1-锁定）
    private LocalDateTime createTime; // datetime - 创建时间
    private LocalDateTime updateTime; // datetime - 更新时间
    private String email;         // varchar(255) - 邮箱
    private String phone;         // varchar(255) - 电话
    private String avatar;        // varchar(255) - 头像（可为空）
    private String role;          // varchar(255) - 身份
    private Integer status;       // tinyint - 状态
}