package org.example.ordercartservice.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemVO {
    private Long id;
    private Long productId;
    private Long skuId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer count;
    private Boolean selected;

    // 实时信息
    private Integer stock;           // 当前库存
    private Boolean valid;           // 是否有效（库存充足且商品上架）
    private Boolean priceChanged;    // 价格是否变动
    private BigDecimal currentPrice; // 当前价格
}