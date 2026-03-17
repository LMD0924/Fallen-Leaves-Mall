package org.example.merchantbackend.service.Impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.merchantbackend.dto.ProductAddDTO;
import org.example.merchantbackend.dto.ProductUpdateDTO;
import org.example.productservice.entity.Product;
import org.example.productservice.entity.ProductSku;
import org.example.productservice.mapper.ProductMapper;
import org.example.productservice.mapper.ProductSkuMapper;
import org.example.productservice.vo.ProductDetailVO;
import org.example.productservice.vo.SkuVO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductAdminService {

    private final ProductMapper productMapper;
    private final ProductSkuMapper skuMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PRODUCT_DETAIL_KEY = "product:detail:";

    @Transactional(rollbackFor = Exception.class)
    public ProductDetailVO addProduct(ProductAddDTO dto) {
        // 1. 添加商品主表
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);

        product.setSoldCount(0);  // 新商品销量为0

        productMapper.insert(product);

        // 2. 如果有SKU，添加SKU
        if (dto.getSkuList() != null && !dto.getSkuList().isEmpty()) {
            List<ProductSku> skuList = dto.getSkuList().stream().map(skuItem -> {
                ProductSku sku = new ProductSku();
                BeanUtils.copyProperties(skuItem, sku);
                sku.setProductId(product.getId());
                return sku;
            }).collect(Collectors.toList());

            skuMapper.batchInsert(skuList);
        }

        log.info("添加商品成功: productId={}, name={}", product.getId(), product.getName());

        return getProductDetail(product.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductDetailVO updateProduct(@Valid ProductUpdateDTO dto) {
        Product product = productMapper.selectById(dto.getId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        BeanUtils.copyProperties(dto, product, "id", "soldCount");
        productMapper.updateById(product);

        // 清除缓存
        redisTemplate.delete(PRODUCT_DETAIL_KEY + product.getId());

        return getProductDetail(product.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 删除SKU
        skuMapper.deleteByProductId(productId);

        // 删除商品
        productMapper.deleteById(productId);

        // 清除缓存
        redisTemplate.delete(PRODUCT_DETAIL_KEY + productId);

        log.info("删除商品成功: productId={}", productId);
        return true;
    }

    public boolean updateProductStatus(Long productId, Integer status) {
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        int updated = productMapper.updateById(product);

        if (updated > 0) {
            redisTemplate.delete(PRODUCT_DETAIL_KEY + productId);
        }

        return updated > 0;
    }

    private ProductDetailVO getProductDetail(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return null;
        }

        ProductDetailVO vo = new ProductDetailVO();
        BeanUtils.copyProperties(product, vo);

        // 查询SKU列表
        List<ProductSku> skuList = skuMapper.selectByProductId(productId);

        // 转换为SkuVO列表
        List<SkuVO> skuVOList = skuList.stream().map(sku -> {
            SkuVO skuVO = new SkuVO();
            BeanUtils.copyProperties(sku, skuVO);

            // 解析规格JSON为易读格式
            // 解析规格
            if (sku.getSpecs() != null) {
                try {
                    // 方法1：直接转成指定的Map类型
                    Map<String, String> specsMap = com.alibaba.fastjson.JSON.parseObject(
                            sku.getSpecs(),
                            new com.alibaba.fastjson.TypeReference<Map<String, String>>(){}
                    );
                    skuVO.setSpecsMap(specsMap);

                    // 拼接规格文本
                    String specsText = String.join(" ", specsMap.values());
                    skuVO.setSpecsText(specsText);

                } catch (Exception e) {
                    skuVO.setSpecsText(sku.getSpecs());
                }
            }

            return skuVO;
        }).collect(Collectors.toList());

        vo.setSkuList(skuVOList);

        return vo;
    }
}