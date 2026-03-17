package org.example.merchantbackend.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "优惠券适用范围视图对象")
public class CouponScopeVO {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "优惠券ID", example = "1")
    private Long couponId;

    @Schema(description = "范围ID（商品ID或分类ID）", example = "1001")
    private Long scopeId;

    @Schema(description = "范围类型：2商品 3分类", example = "2")
    private Integer scopeType;

    @Schema(description = "范围名称", example = "华为Mate 60 Pro")
    private String scopeName;

    @Schema(description = "范围图片", example = "http://image.luoye.com/product/1.jpg")
    private String scopeImage;
}