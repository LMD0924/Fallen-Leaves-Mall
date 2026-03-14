package org.example.ordercartservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "订单项视图对象")
public class OrderItemVO {

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

    @Schema(description = "SKU规格JSON", example = "{\"颜色\":\"红色\",\"尺寸\":\"M\"}")
    private String skuSpecs;

    @Schema(description = "SKU规格文本", example = "红色 M")
    private String skuSpecsText;

    @Schema(description = "价格", example = "6999.00")
    private BigDecimal price;

    @Schema(description = "数量", example = "1")
    private Integer count;

    @Schema(description = "总金额", example = "6999.00")
    private BigDecimal totalAmount;

    @Schema(description = "商品总重量(克)", example = "200")
    private Integer totalWeight;

    @Schema(description = "是否评价", example = "false")
    private Boolean reviewed;

    @Schema(description = "售后状态", example = "0")
    private Integer afterSaleStatus;

    /**
     * 获取规格文本
     */
    public String getSkuSpecsText() {
        if (skuSpecs == null) return "";
        try {
            com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSON.parseObject(skuSpecs);
            return String.join(" ", json.values().stream()
                    .map(Object::toString)
                    .collect(java.util.stream.Collectors.toList()));
        } catch (Exception e) {
            return skuSpecs;
        }
    }
}