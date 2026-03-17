package org.example.merchantbackend.controller;

import org.example.backend.common.RestBean;
import org.example.merchantbackend.dto.ProductAddDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.merchantbackend.dto.ProductUpdateDTO;
import org.example.merchantbackend.service.Impl.ProductAdminService;
import org.example.productservice.vo.ProductDetailVO;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台商品管理")
@RestController
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductAdminService productAdminService;

    @Operation(summary = "添加商品")
    @PostMapping("/add")
    //@PreAuthorize("hasRole('ADMIN')")
    public RestBean<ProductDetailVO> addProduct(@Valid @RequestBody ProductAddDTO dto) {
        ProductDetailVO product = productAdminService.addProduct(dto);
        return RestBean.success(product);
    }

    @Operation(summary = "更新商品")
    @PutMapping("/update")
    //@PreAuthorize("hasRole('ADMIN')")
    public RestBean<ProductDetailVO> updateProduct(@Valid @RequestBody ProductUpdateDTO dto) {
        ProductDetailVO product = productAdminService.updateProduct(dto);
        return RestBean.success(product);
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/delete/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public RestBean<Boolean> deleteProduct(@PathVariable Long id) {
        boolean success = productAdminService.deleteProduct(id);
        return RestBean.success(success);
    }

    @Operation(summary = "上架/下架商品")
    @PutMapping("/status/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public RestBean<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = productAdminService.updateProductStatus(id, status);
        return RestBean.success(success);
    }
}