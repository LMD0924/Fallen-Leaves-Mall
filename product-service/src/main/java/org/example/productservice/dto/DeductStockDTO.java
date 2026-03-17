package org.example.productservice.dto;

import lombok.Data;

import java.util.List;

/*
 * @Author:总会落叶
 * @Date:2026/3/17
 * @Description:
 */
@Data
public class DeductStockDTO {
    private String orderNo;
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private Long productId;
        private Long skuId;
        private Integer count;
    }
}
