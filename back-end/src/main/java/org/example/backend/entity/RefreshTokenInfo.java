package org.example.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/*
 * @Author:总会落叶
 * @Date:2026/2/13
 * @Description: Refresh Token 信息（存入Redis）
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String tokenId;           // Token ID（UUID）
    private Long userId;               // 用户ID
    private String username;           // 用户名
    private String deviceInfo;         // 设备信息
    private String ipAddress;          // IP地址
    private String userAgent;          // User-Agent
    private Date loginTime;            // 登录时间
    private Date lastRefreshTime;      // 最后刷新时间
    private Integer refreshCount;      // 刷新次数
    private Boolean isValid;           // 是否有效
}
