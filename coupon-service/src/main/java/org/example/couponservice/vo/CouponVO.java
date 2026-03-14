package org.example.couponservice.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponVO {
    private Long id;
    private String name;
    private Integer type;
    private String typeText;      // 满减券/折扣券/无门槛券
    private BigDecimal faceValue;
    private BigDecimal condition;
    private Integer stock;        // 实时库存
    private Integer totalCount;
    private Integer receiveCount;
    private LocalDateTime receiveStartTime;
    private LocalDateTime receiveEndTime;
    private LocalDateTime useStartTime;
    private LocalDateTime useEndTime;
    private Integer perUserLimit;
    private String description;
    private Integer scopeType;
    private String scopeTypeText; // 全场/指定商品/指定分类

    public String getTypeText() {
        switch (type) {
            case 1: return "满减券";
            case 2: return "折扣券";
            case 3: return "无门槛券";
            default: return "未知";
        }
    }

    public String getScopeTypeText() {
        switch (scopeType) {
            case 1: return "全场通用";
            case 2: return "指定商品";
            case 3: return "指定分类";
            default: return "未知";
        }
    }
}