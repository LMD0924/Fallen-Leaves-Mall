package org.example.merchantbackend.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "优惠券详情视图对象")
public class CouponDetailVO {

    @Schema(description = "优惠券ID", example = "1")
    private Long id;

    @Schema(description = "优惠券名称", example = "新人专享满减券")
    private String name;

    @Schema(description = "优惠券类型：1满减券 2折扣券 3无门槛券", example = "1")
    private Integer type;

    @Schema(description = "类型文字", example = "满减券")
    private String typeText;

    @Schema(description = "面值", example = "20.00")
    private BigDecimal faceValue;

    @Schema(description = "使用条件（满多少可用）", example = "100.00")
    private BigDecimal condition;

    @Schema(description = "发行总量", example = "1000")
    private Integer totalCount;

    @Schema(description = "已领取数量", example = "500")
    private Integer receiveCount;

    @Schema(description = "剩余库存", example = "500")
    private Integer stock;

    @Schema(description = "状态：1上架 0下架", example = "1")
    private Integer status;

    @Schema(description = "状态文字", example = "上架")
    private String statusText;

    @Schema(description = "领取开始时间")
    private LocalDateTime receiveStartTime;

    @Schema(description = "领取结束时间")
    private LocalDateTime receiveEndTime;

    @Schema(description = "使用开始时间")
    private LocalDateTime useStartTime;

    @Schema(description = "使用结束时间")
    private LocalDateTime useEndTime;

    @Schema(description = "每人限领数量", example = "1")
    private Integer perUserLimit;

    @Schema(description = "优惠券描述", example = "新用户专享优惠券")
    private String description;

    @Schema(description = "适用范围类型：1全场 2指定商品 3指定分类", example = "1")
    private Integer scopeType;

    @Schema(description = "适用范围文字", example = "全场通用")
    private String scopeTypeText;

    @Schema(description = "适用范围列表（指定商品/分类时）")
    private List<CouponScopeVO> scopes;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "是否可领取", example = "true")
    private Boolean receivable;

    @Schema(description = "剩余天数", example = "5")
    private Long remainDays;

    /**
     * 获取类型文字
     */
    public String getTypeText() {
        if (type == null) return "";
        switch (type) {
            case 1: return "满减券";
            case 2: return "折扣券";
            case 3: return "无门槛券";
            default: return "未知";
        }
    }

    /**
     * 获取状态文字
     */
    public String getStatusText() {
        return status != null && status == 1 ? "上架" : "下架";
    }

    /**
     * 获取适用范围文字
     */
    public String getScopeTypeText() {
        if (scopeType == null) return "";
        switch (scopeType) {
            case 1: return "全场通用";
            case 2: return "指定商品";
            case 3: return "指定分类";
            default: return "未知";
        }
    }

    /**
     * 判断是否可领取
     */
    public Boolean getReceivable() {
        LocalDateTime now = LocalDateTime.now();
        return status == 1 &&
                stock > 0 &&
                now.isAfter(receiveStartTime) &&
                now.isBefore(receiveEndTime);
    }

    /**
     * 获取剩余天数（领取结束）
     */
    public Long getRemainDays() {
        if (receiveEndTime == null) return null;
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(receiveEndTime)) return 0L;

        return java.time.Duration.between(now, receiveEndTime).toDays();
    }
}