package org.example.ordercartservice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "订单项预览视图对象（用于列表页）")
public class OrderItemPreviewVO {

    @Schema(description = "订单项ID", example = "1001")
    private Long id;

    @Schema(description = "商品ID", example = "1")
    private Long productId;

    @Schema(description = "SKU ID", example = "1001")
    private Long skuId;

    @Schema(description = "商品名称", example = "华为Mate 60 Pro")
    private String productName;

    @Schema(description = "商品图片", example = "http://image.luoye.com/product/1/main.jpg")
    private String productImage;

    @Schema(description = "SKU规格文本", example = "红色 M")
    private String skuSpecsText;

    @Schema(description = "价格", example = "6999.00")
    private BigDecimal price;

    @Schema(description = "数量", example = "1")
    private Integer count;

    @Schema(description = "总金额", example = "6999.00")
    private BigDecimal totalAmount;
}