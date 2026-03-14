package org.example.productservice.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.common.RestBean;
import org.example.productservice.dto.ProductQueryDTO;
import org.example.productservice.service.ProductService;
import org.example.productservice.vo.ProductDetailVO;
import org.example.productservice.vo.ProductListVO;
import org.example.productservice.vo.SkuDeduct;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品模块")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "商品列表")
    @GetMapping("/list")
    public RestBean<Page<ProductListVO>> list(@Valid ProductQueryDTO queryDTO) {
        Page<ProductListVO> page = productService.getProductList(queryDTO);
        return RestBean.success(page);
    }

    @Operation(summary = "商品详情")
    @GetMapping("/detail/{id}")
    public RestBean<ProductDetailVO> detail(@PathVariable Long id) {
        ProductDetailVO detail = productService.getProductDetail(id);
        if (detail == null) {
            return RestBean.failure("商品不存在");
        }
        return RestBean.success(detail);
    }

    @Operation(summary = "扣减库存（秒杀用）")
    @PostMapping("/deductStock")
    public RestBean<Boolean> deductStock(@RequestParam Long productId,
                                             @RequestParam Integer count,
                                             @RequestBody List<SkuDeduct> skuList) {
        boolean success = productService.deductStock(productId, count, skuList);
        if (!success) {
            return RestBean.failure("库存不足");
        }
        return RestBean.success(true);
    }

    @Operation(summary = "预热商品库存")
    @PostMapping("/preheat/{id}")
    public RestBean<Void> preheat(@PathVariable Long id) {
        productService.preheatStock(id);
        return RestBean.success(null);
    }
}