package org.example.couponservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("coupon")
public class Coupon {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private Integer type;           // 1满减券 2折扣券 3无门槛券
    private BigDecimal faceValue;   // 面值
    @TableField("`condition`")
    private BigDecimal condition;   // 使用条件
    private Integer totalCount;     // 发行总量
    private Integer receiveCount;   // 已领取
    private Integer stock;          // 剩余库存
    private Integer status;         // 1上架 0下架
    private LocalDateTime receiveStartTime;
    private LocalDateTime receiveEndTime;
    private LocalDateTime useStartTime;
    private LocalDateTime useEndTime;
    private Integer perUserLimit;   // 每人限领
    private String description;
    private Integer scopeType;      // 1全场 2指定商品 3指定分类

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime = LocalDateTime.now();  // 设置默认值

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime = LocalDateTime.now();  // 设置默认值
}