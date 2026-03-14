package org.example.productservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.productservice.entity.ProductSku;
import org.example.productservice.vo.SkuDeduct;

import java.util.List;

@Mapper
public interface ProductSkuMapper extends BaseMapper<ProductSku> {

    /**
     * 批量扣减SKU库存
     */
    @Update("<script>" +
            "<foreach collection='list' item='item' separator=';'>" +
            "UPDATE product_sku SET stock = stock - #{item.count} " +
            "WHERE id = #{item.skuId} AND stock >= #{item.count}" +
            "</foreach>" +
            "</script>")
    int batchDeductStock(@Param("list") List<SkuDeduct> list);

    /**
     * 根据商品ID查询所有SKU
     */
    @Select("SELECT * FROM product_sku WHERE product_id = #{productId}")
    List<ProductSku> selectByProductId(Long productId);
}