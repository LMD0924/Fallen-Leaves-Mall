package org.example.couponservice.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserCouponVO {
    private Long id;
    private Long userId;
    private Long couponId;
    private String couponName;
    private String code;
    private Integer type;
    private String typeText;
    private BigDecimal faceValue;
    private BigDecimal condition;
    private Integer status;
    private String statusText;
    private LocalDateTime receiveTime;
    private LocalDateTime useTime;
    private Long orderId;
    private String orderNo;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // 临时字段（计算优惠）
    private BigDecimal discountAmount;

    public String getTypeText() {
        switch (type) {
            case 1: return "满减券";
            case 2: return "折扣券";
            case 3: return "无门槛券";
            default: return "未知";
        }
    }

    public String getStatusText() {
        switch (status) {
            case 0: return "未使用";
            case 1: return "已使用";
            case 2: return "已过期";
            case 3: return "已冻结";
            default: return "未知";
        }
    }
}