package org.example.couponservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("coupon_scope")
public class CouponScope {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long couponId;
    private Long scopeId;        // 商品ID或分类ID
    private Integer scopeType;   // 2商品 3分类

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}