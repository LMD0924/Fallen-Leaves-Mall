package org.example.seckillsession.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("seckill_order")
public class SeckillOrder {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long seckillId;
    private Long sessionId;
    private Long productId;
    private Long skuId;
    private Long userId;
    private Long orderId;
    private String orderNo;
    private BigDecimal seckillPrice;
    private Integer quantity;
    private Integer status;  // 0预下单 1已支付 2已取消 3已过期

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private LocalDateTime payTime;
    private LocalDateTime cancelTime;
}