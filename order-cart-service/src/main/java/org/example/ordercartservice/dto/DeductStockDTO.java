package org.example.ordercartservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "扣减库存参数")
public class DeductStockDTO {

    @Schema(description = "订单号", example = "20240301123456789")
    private String orderNo;

    @Schema(description = "订单商品列表")
    private List<OrderItem> items;

    @Data
    public static class OrderItem {
        @Schema(description = "商品ID", example = "1")
        private Long productId;

        @Schema(description = "SKU ID", example = "1")
        private Long skuId;

        @Schema(description = "购买数量", example = "1")
        private Integer count;
    }
}