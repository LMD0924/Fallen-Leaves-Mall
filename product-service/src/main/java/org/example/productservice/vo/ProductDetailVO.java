package org.example.productservice.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDetailVO {
    private Long id;
    private String name;
    private String subtitle;
    private BigDecimal price;
    private Integer stock;
    private Integer soldCount;
    private String mainImage;
    private String detail;
    private List<SkuVO> skuList;
}