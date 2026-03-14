package org.example.productservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "商品列表视图对象（增强版）")
public class ProductListVO {

    @Schema(description = "商品ID", example = "1")
    private Long id;

    @Schema(description = "商品名称", example = "华为Mate 60 Pro")
    private String name;

    @Schema(description = "副标题", example = "鸿蒙系统 卫星通话")
    private String subtitle;

    @Schema(description = "分类ID", example = "1001")
    private Long categoryId;

    @Schema(description = "分类名称", example = "手机")
    private String categoryName;

    @Schema(description = "分类层级路径", example = "电子产品/手机")
    private String categoryPath;

    @Schema(description = "品牌ID", example = "101")
    private Long brandId;

    @Schema(description = "品牌名称", example = "华为")
    private String brandName;

    @Schema(description = "品牌Logo", example = "http://image.luoye.com/brand/huawei.png")
    private String brandLogo;

    @Schema(description = "价格", example = "6999.00")
    private BigDecimal price;

    @Schema(description = "原价（划线价）", example = "7999.00")
    private BigDecimal originalPrice;

    @Schema(description = "最低价格（SKU最低价）", example = "5999.00")
    private BigDecimal minPrice;

    @Schema(description = "最高价格（SKU最高价）", example = "7999.00")
    private BigDecimal maxPrice;

    @Schema(description = "是否有多个规格", example = "true")
    private Boolean hasSku;

    @Schema(description = "库存", example = "1000")
    private Integer stock;

    @Schema(description = "销量", example = "500")
    private Integer soldCount;

    @Schema(description = "主图", example = "http://image.luoye.com/product/1/main.jpg")
    private String mainImage;

    @Schema(description = "轮播图列表")
    private List<String> images;

    @Schema(description = "评分", example = "4.8")
    private Double rating;

    @Schema(description = "评论数", example = "200")
    private Integer reviewCount;

    @Schema(description = "好评率", example = "98%")
    private String positiveRate;

    @Schema(description = "标签", example = "新品,热销")
    private String tags;

    @Schema(description = "标签列表")
    private List<String> tagList;

    @Schema(description = "状态：1上架 0下架", example = "1")
    private Integer status;

    @Schema(description = "状态文字", example = "上架")
    private String statusText;

    @Schema(description = "是否新品", example = "true")
    private Boolean isNew;

    @Schema(description = "是否热销", example = "true")
    private Boolean isHot;

    @Schema(description = "是否推荐", example = "true")
    private Boolean isRecommend;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 折扣相关
    @Schema(description = "折扣率", example = "0.88")
    private Double discount;

    @Schema(description = "是否为秒杀商品", example = "false")
    private Boolean isSeckill;

    @Schema(description = "秒杀价", example = "5999.00")
    private BigDecimal seckillPrice;

    @Schema(description = "秒杀开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime seckillStartTime;

    @Schema(description = "秒杀结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime seckillEndTime;

    @Schema(description = "运费", example = "0.00")
    private BigDecimal freight;

    @Schema(description = "发货地", example = "广东深圳")
    private String deliveryArea;

    @Schema(description = "商品描述", example = "商品简要描述")
    private String brief;

    /**
     * 计算折扣率
     */
    public Double getDiscount() {
        if (originalPrice != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
            return price.divide(originalPrice, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return 1.0;
    }

    /**
     * 获取标签列表
     */
    public List<String> getTagList() {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        return List.of(tags.split(","));
    }

    /**
     * 获取状态文字
     */
    public String getStatusText() {
        return status != null && status == 1 ? "上架" : "下架";
    }

    /**
     * 判断是否有多个规格
     */
    public Boolean getHasSku() {
        return minPrice != null && maxPrice != null && !minPrice.equals(maxPrice);
    }
}