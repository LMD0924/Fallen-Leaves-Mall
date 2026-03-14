package org.example.ordercartservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CreateOrderDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotEmpty(message = "请选择要购买的商品")
    private List<OrderItemDTO> items;

    @NotBlank(message = "收货人不能为空")
    private String receiverName;

    @NotBlank(message = "联系电话不能为空")
    private String receiverPhone;

    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;

    private String remark;

    private Boolean fromCart;  // 是否来自购物车
}