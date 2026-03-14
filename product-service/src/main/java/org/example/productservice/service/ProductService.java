package org.example.productservice.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.productservice.dto.ProductQueryDTO;
import org.example.productservice.vo.ProductDetailVO;
import org.example.productservice.vo.ProductListVO;
import org.example.productservice.vo.SkuDeduct;

import java.util.List;

public interface ProductService {

    /**
     * 商品列表（带缓存）
     */
    Page<ProductListVO> getProductList(ProductQueryDTO queryDTO);

    /**
     * 商品详情（带缓存）
     */
    ProductDetailVO getProductDetail(Long productId);

    /**
     * 扣减库存（高并发场景）
     */
    boolean deductStock(Long productId, Integer count, List<SkuDeduct> skuList);

    /**
     * 预热商品库存到Redis（秒杀用）
     */
    void preheatStock(Long productId);
}