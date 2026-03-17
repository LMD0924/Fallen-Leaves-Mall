package org.example.productservice.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.productservice.dto.DeductStockForOrderDTO;
import org.example.productservice.dto.ProductQueryDTO;
import org.example.productservice.dto.RestoreStockDTO;
import org.example.productservice.vo.ProductDetailVO;
import org.example.productservice.vo.ProductListVO;
import org.example.productservice.vo.ProductSkuVO;
import org.example.productservice.vo.SkuDeduct;

import java.util.List;
import java.util.Map;

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

    /**
     * 获取SKU信息
     */
    ProductSkuVO getSkuInfo(Long skuId);

    /**
     * 批量获取SKU信息
     */
    Map<Long, ProductSkuVO> batchGetSkuInfo(List<Long> skuIds);

    /**
     * 恢复库存（取消订单时调用）
     */
    boolean restoreStock(RestoreStockDTO dto);

    /**
     * 普通订单扣减库存（非秒杀）
     */
    boolean deductStockForOrder(DeductStockForOrderDTO dto);
}