package org.example.merchantbackend.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "更新优惠券参数")
public class CouponUpdateDTO {

    @NotNull(message = "优惠券ID不能为空")
    @Schema(description = "优惠券ID", required = true, example = "1")
    private Long id;

    @NotBlank(message = "优惠券名称不能为空")
    @Schema(description = "优惠券名称", example = "新人专享满减券")
    private String name;

    @NotNull(message = "优惠券类型不能为空")
    @Min(value = 1, message = "类型值错误")
    @Max(value = 3, message = "类型值错误")
    @Schema(description = "优惠券类型：1满减券 2折扣券 3无门槛券", example = "1")
    private Integer type;

    @NotNull(message = "面值不能为空")
    @DecimalMin(value = "0.01", message = "面值必须大于0")
    @Schema(description = "面值", example = "20.00")
    private BigDecimal faceValue;

    @DecimalMin(value = "0", message = "使用条件不能小于0")
    @Schema(description = "使用条件（满多少可用）", example = "100.00")
    @TableField("`condition`")
    private BigDecimal condition;

    @NotNull(message = "发行总量不能为空")
    @Min(value = 1, message = "发行总量必须大于0")
    @Schema(description = "发行总量", example = "1000")
    private Integer totalCount;

    @NotNull(message = "每人限领数量不能为空")
    @Min(value = 1, message = "每人限领数量必须大于0")
    @Schema(description = "每人限领数量", example = "1")
    private Integer perUserLimit;

    @NotNull(message = "领取开始时间不能为空")
    @Schema(description = "领取开始时间")
    private LocalDateTime receiveStartTime;

    @NotNull(message = "领取结束时间不能为空")
    @Schema(description = "领取结束时间")
    private LocalDateTime receiveEndTime;

    @NotNull(message = "使用开始时间不能为空")
    @Schema(description = "使用开始时间")
    private LocalDateTime useStartTime;

    @NotNull(message = "使用结束时间不能为空")
    @Schema(description = "使用结束时间")
    private LocalDateTime useEndTime;

    @Schema(description = "优惠券描述", example = "新用户专享优惠券")
    private String description;

    @NotNull(message = "适用范围类型不能为空")
    @Schema(description = "适用范围类型：1全场 2指定商品 3指定分类", example = "1")
    private Integer scopeType;

    @Schema(description = "适用范围ID列表（指定商品/分类时必填）")
    private List<Long> scopeIds;

    // 以下字段可选更新
    @Schema(description = "状态：1上架 0下架", example = "1")
    private Integer status;
}