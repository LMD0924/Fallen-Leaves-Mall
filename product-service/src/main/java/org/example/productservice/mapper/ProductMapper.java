package org.example.productservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.productservice.entity.Product;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 扣减库存（乐观锁实现）
     */
    @Update("UPDATE product SET stock = stock - #{count}, " +
            "sold_count = sold_count + #{count} " +
            "WHERE id = #{productId} AND stock >= #{count}")
    int deductStock(Long productId, Integer count);

    /**
     * 获取商品详情（包含缓存标记）
     */
    @Select("SELECT * FROM product WHERE id = #{id} AND status = 1")
    Product getActiveProduct(Long id);
}