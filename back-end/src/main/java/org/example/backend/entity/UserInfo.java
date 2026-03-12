package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.commonbackend.code.UserStatus;

import java.time.LocalDateTime;

/*
 * @Author:总会落叶
 * @Date:2026/3/11
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_account")
public class UserInfo {
    private Integer id;
    private Long userId; //用户id
    private Double balance; //余额
    private Long points; //积分
    private UserStatus vipLevel; //会员等级
    private LocalDateTime vipExpireTime; //会员到期时间
}
