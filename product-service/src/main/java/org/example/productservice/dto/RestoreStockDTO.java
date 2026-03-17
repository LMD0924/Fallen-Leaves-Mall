package org.example.productservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class RestoreStockDTO {

    private String orderNo;          // 订单号
    private Long userId;              // 用户ID
    private List<RestoreItem> items;  // 恢复商品列表
    private String reason;             // 恢复原因（如：订单取消）

    @Data
    public static class RestoreItem {
        private Long productId;        // 商品ID
        private Long skuId;            // SKU ID
        private Integer count;          // 恢复数量
    }
}