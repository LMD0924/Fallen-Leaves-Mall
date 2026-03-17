package org.example.productservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
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
    List<ProductSku> selectByProductId(@Param("productId") Long productId);

    /**
     * 批量插入SKU
     */
    @Insert("<script>" +
            "INSERT INTO product_sku (product_id, specs, price, stock, code, image, create_time, update_time) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.productId}, #{item.specs}, #{item.price}, #{item.stock}, #{item.code}, #{item.image}, NOW(), NOW())" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<ProductSku> skuList);

    /**
     * 根据商品ID删除SKU
     */
    @Delete("DELETE FROM product_sku WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);

    /**
     * 恢复SKU库存
     */
    @Update("UPDATE product_sku SET stock = stock + #{count} WHERE id = #{skuId}")
    int restoreStock(@Param("skuId") Long skuId, @Param("count") Integer count);


    @Update("UPDATE product_sku SET stock = stock - #{count} WHERE id = #{skuId} AND stock >= #{count}")
    int deductStock(@Param("skuId") Long skuId, @Param("count") Integer count);
}