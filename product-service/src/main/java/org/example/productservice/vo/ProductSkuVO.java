package org.example.productservice.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ProductSkuVO {

    private Long id;                    // SKU ID
    private Long productId;              // 商品ID
    private String productName;          // 商品名称
    private String specs;                // 规格JSON
    private Map<String, String> specsMap; // 规格Map
    private String specsText;             // 规格文本
    private BigDecimal price;             // 价格
    private Integer stock;                // 库存
    private String code;                  // SKU编码
    private String image;                  // SKU图片
    private Integer status;                // 状态：1上架 0下架

    /**
     * 获取规格文本
     */
    public String getSpecsText() {
        if (this.specs == null) {
            return "";
        }
        try {
            com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSON.parseObject(specs);
            return String.join(" ", json.values().stream()
                    .map(Object::toString)
                    .collect(java.util.stream.Collectors.toList()));
        } catch (Exception e) {
            return specs;
        }
    }

    /**
     * 获取规格Map
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
}