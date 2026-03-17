package org.example.couponservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("coupon_receive_log")
public class CouponReceiveLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long couponId;      // 优惠券ID
    private Long userId;        // 用户ID
    private LocalDateTime receiveTime;  // 领取时间
    private Integer status;      // 结果：1成功 2失败
    private String message;      // 失败原因
    private String ip;           // 领取IP
    private String userAgent;    // 用户设备信息
}