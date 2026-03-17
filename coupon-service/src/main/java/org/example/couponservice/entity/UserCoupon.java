package org.example.couponservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_coupon")
public class UserCoupon {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long couponId;
    private String code;            // 唯一券码
    private BigDecimal faceValue;
    @TableField("`condition`")
    private BigDecimal condition;
    private Integer type;
    private Integer status;         // 0未使用 1已使用 2已过期 3已冻结
    private LocalDateTime receiveTime;
    private LocalDateTime useTime;
    private Long orderId;
    private String orderNo;
    private LocalDateTime startTime = LocalDateTime.now();
    private LocalDateTime endTime = LocalDateTime.now().plusDays(30);

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}