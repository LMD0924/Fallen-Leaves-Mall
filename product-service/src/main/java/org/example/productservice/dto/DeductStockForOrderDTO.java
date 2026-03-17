package org.example.productservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class DeductStockForOrderDTO {

    private String orderNo;           // 订单号
    private List<OrderItem> items;    // 订单商品列表

    @Data
    public static class OrderItem {
        private Long productId;        // 商品ID
        private Long skuId;             // SKU ID
        private Integer count;           // 购买数量
    }
}