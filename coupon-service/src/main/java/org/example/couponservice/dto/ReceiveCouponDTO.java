package org.example.couponservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReceiveCouponDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "优惠券ID不能为空")
    private Long couponId;
}