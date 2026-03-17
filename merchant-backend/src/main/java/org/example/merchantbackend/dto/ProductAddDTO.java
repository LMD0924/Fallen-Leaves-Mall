package org.example.merchantbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "添加商品参数")
public class ProductAddDTO {

    @NotBlank(message = "商品名称不能为空")
    @Schema(description = "商品名称", required = true, example = "华为Mate 60 Pro")
    private String name;

    @Schema(description = "副标题", example = "鸿蒙系统 卫星通话")
    private String subtitle;

    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID", required = true, example = "1001")
    private Long categoryId;

    @NotNull(message = "品牌ID不能为空")
    @Schema(description = "品牌ID", required = true, example = "101")
    private Long brandId;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    @Schema(description = "价格", required = true, example = "6999.00")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能小于0")
    @Schema(description = "库存", required = true, example = "1000")
    private Integer stock;

    @Schema(description = "主图", example = "http://image.luoye.com/product/1/main.jpg")
    private String mainImage;

    @Schema(description = "商品详情（富文本）")
    private String detail;

    @Schema(description = "状态：1上架 0下架", example = "1")
    private Integer status = 1;

    @Schema(description = "SKU列表（多规格商品）")
    private List<SkuItem> skuList;

    @Data
    @Schema(description = "SKU项")
    public static class SkuItem {

        @Schema(description = "规格JSON", example = "{\"颜色\":\"红色\",\"尺寸\":\"M\"}")
        private String specs;

        @Schema(description = "SKU价格", example = "6999.00")
        private BigDecimal price;

        @Schema(description = "SKU库存", example = "100")
        private Integer stock;

        @Schema(description = "SKU编码", example = "SKU2024001")
        private String code;

        @Schema(description = "SKU图片", example = "http://image.luoye.com/sku/1001.jpg")
        private String image;
    }
}