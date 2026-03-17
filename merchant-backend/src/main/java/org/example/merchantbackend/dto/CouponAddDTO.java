package org.example.merchantbackend.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CouponAddDTO {

    @NotBlank(message = "优惠券名称不能为空")
    private String name;

    @NotNull(message = "优惠券类型不能为空")
    @Min(value = 1)
    @Max(value = 3)
    private Integer type;  // 1满减券 2折扣券 3无门槛券

    @NotNull(message = "面值不能为空")
    @DecimalMin(value = "0.01")
    private BigDecimal faceValue;

    @TableField("`condition`")
    private BigDecimal condition;  // 使用条件

    @NotNull(message = "发行总量不能为空")
    @Min(value = 1)
    private Integer totalCount;

    @NotNull(message = "每人限领数量不能为空")
    @Min(value = 1)
    private Integer perUserLimit;

    @NotNull(message = "领取开始时间不能为空")
    private LocalDateTime receiveStartTime;

    @NotNull(message = "领取结束时间不能为空")
    private LocalDateTime receiveEndTime;

    @NotNull(message = "使用开始时间不能为空")
    private LocalDateTime useStartTime;

    @NotNull(message = "使用结束时间不能为空")
    private LocalDateTime useEndTime;

    private String description;

    @NotNull(message = "适用范围类型不能为空")
    private Integer scopeType;  // 1全场 2指定商品 3指定分类

    private List<Long> scopeIds;  // 商品ID列表或分类ID列表
}