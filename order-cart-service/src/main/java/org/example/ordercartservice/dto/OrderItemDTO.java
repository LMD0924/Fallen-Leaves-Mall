package org.example.ordercartservice.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Long skuId;
    private Integer count;
}