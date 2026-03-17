package org.example.ordercartservice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "商品SKU视图对象")
public class ProductSkuVO {

    @Schema(description = "SKU ID", example = "1001")
    private Long id;

    @Schema(description = "商品ID", example = "1")
    private Long productId;

    @Schema(description = "商品名称", example = "华为Mate 60 Pro")
    private String productName;

    @Schema(description = "SKU编码", example = "SKU2024001")
    private String code;

    @Schema(description = "规格JSON", example = "{\"颜色\":\"红色\",\"尺寸\":\"M\"}")
    private String specs;

    @Schema(description = "规格Map", example = "{\"颜色\":\"红色\",\"尺寸\":\"M\"}")
    private Map<String, String> specsMap;

    @Schema(description = "规格列表")
    private List<SpecItem> specList;

    @Schema(description = "规格文本", example = "红色 M")
    private String specsText;

    @Schema(description = "价格", example = "6999.00")
    private BigDecimal price;

    @Schema(description = "原价（划线价）", example = "7999.00")
    private BigDecimal originalPrice;

    @Schema(description = "库存", example = "100")
    private Integer stock;

    @Schema(description = "图片", example = "http://image.luoye.com/sku/1001.jpg")
    private String image;

    @Schema(description = "图片列表")
    private List<String> images;

    @Schema(description = "重量(克)", example = "200")
    private Integer weight;

    @Schema(description = "体积(立方厘米)", example = "1000")
    private Integer volume;

    @Schema(description = "销量", example = "50")
    private Integer soldCount;

    @Schema(description = "状态：1上架 0下架", example = "1")
    private Integer status;

    @Schema(description = "状态文字", example = "上架")
    private String statusText;

    @Schema(description = "是否默认", example = "false")
    private Boolean isDefault;


    private String detail;

    /**
     * 规格项内部类
     */
    @Data
    @Schema(description = "规格项")
    public static class SpecItem {
        @Schema(description = "规格名称", example = "颜色")
        private String name;

        @Schema(description = "规格值", example = "红色")
        private String value;

        @Schema(description = "规格图片（颜色图片等）", example = "http://image.luoye.com/spec/red.jpg")
        private String image;
    }

    /**
     * 解析规格JSON为Map
     */
    public Map<String, String> getSpecsMap() {
        if (this.specs == null) {
            return null;
        }
        try {
            return com.alibaba.fastjson.JSON.parseObject(specs, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析规格JSON为列表
     */
    public List<SpecItem> getSpecList() {
        Map<String, String> map = getSpecsMap();
        if (map == null) {
            return new ArrayList<>();
        }

        List<SpecItem> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            SpecItem item = new SpecItem();
            item.setName(entry.getKey());
            item.setValue(entry.getValue());
            list.add(item);
        }
        return list;
    }

    /**
     * 获取规格文本
     */
    public String getSpecsText() {
        Map<String, String> map = getSpecsMap();
        if (map == null) {
            return "";
        }
        return String.join(" ", map.values());
    }

    /**
     * 获取状态文字
     */
    public String getStatusText() {
        return status != null && status == 1 ? "上架" : "下架";
    }
}