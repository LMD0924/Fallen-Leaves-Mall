package org.example.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * @Author:总会落叶
 * @Date:2026/2/6
 * @Description: 用户实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String account;
    private String password;
    private String email;
    private String phone;
    private String avatar;
    private String role;
    private String status;
    private String locked;
    private String createTime;
    private String updateTime;
}
