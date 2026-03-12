package org.example.merchantbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.common.RestBean;
import org.example.commonbackend.code.ShopEnum;
import org.example.merchantbackend.entity.Shop;
import org.example.merchantbackend.entity.ShopCategory;
import org.example.merchantbackend.entity.ShopQualification;
import org.example.merchantbackend.service.ShopCategoryService;
import org.example.merchantbackend.service.ShopQualificationService;
import org.example.merchantbackend.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * @Author:总会落叶
 * @Date:2026/2/20
 * @Description: 店铺控制器
 */
@Slf4j
@RestController
@RequestMapping("api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;
    private final ShopCategoryService shopCategoryService;
    private final ShopQualificationService shopQualificationService;

    /**
     * 商家申请店铺
     */
   @PostMapping("/applyShop")
   public RestBean<String> applyShop(@RequestBody Shop shop){
       log.info("商家申请店铺:{}",shop);
       if(shopService.applyShop(shop)){
           return RestBean.success("申请成功，等待审核");
       }
       return RestBean.failure("申请失败");
   }

    /**
     * 管理员审核店铺
     */
    @PutMapping("/review/{shopId}")
    public RestBean<String> adminReviewShop(@PathVariable Integer shopId, @RequestParam ShopEnum shopStatus, @RequestParam(required = false) String rejectReason){
        if(shopService.adminReviewShop(shopId, shopStatus, rejectReason)){
            return RestBean.success("审核成功");
        }
        return RestBean.failure("审核失败");
    }

    // ==================== 店铺管理 ====================

    /**
     * 根据商家ID获取店铺信息
     */
    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<Shop> getShopByMerchantId(@PathVariable Long merchantId) {
        Shop shop = shopService.getShopByMerchantId(merchantId);
        return shop != null ? ResponseEntity.ok(shop) : ResponseEntity.notFound().build();
    }

    /**
     * 根据店铺ID获取店铺信息
     */
    @GetMapping("/{shopId}")
    public ResponseEntity<Shop> getShopById(@PathVariable Long shopId) {
        Shop shop = shopService.getShopById(shopId);
        return shop != null ? ResponseEntity.ok(shop) : ResponseEntity.notFound().build();
    }

    /**
     * 创建店铺
     */
    @PostMapping
    public ResponseEntity<Boolean> createShop(@RequestBody Shop shop) {
        boolean result = shopService.createShop(shop);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新店铺信息
     */
    @PutMapping
    public ResponseEntity<Boolean> updateShop(@RequestBody Shop shop) {
        boolean result = shopService.updateShop(shop);
        return ResponseEntity.ok(result);
    }

    /**
     * 暂停店铺
     */
    @PutMapping("/{shopId}/pause")
    public ResponseEntity<Boolean> pauseShop(@PathVariable Long shopId) {
        boolean result = shopService.pauseShop(shopId);
        return ResponseEntity.ok(result);
    }

    /**
     * 恢复店铺
     */
    @PutMapping("/{shopId}/resume")
    public ResponseEntity<Boolean> resumeShop(@PathVariable Long shopId) {
        boolean result = shopService.resumeShop(shopId);
        return ResponseEntity.ok(result);
    }

    /**
     * 关闭店铺
     */
    @PutMapping("/{shopId}/close")
    public ResponseEntity<Boolean> closeShop(@PathVariable Long shopId) {
        boolean result = shopService.closeShop(shopId);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 设置店铺状态
     */
    @PutMapping("/status/{shopId}")
    public ResponseEntity<Boolean> setShopStatus(@PathVariable Long shopId, @RequestParam Integer status) {
        Shop shop = new Shop();
        shop.setId(shopId);
        ShopEnum statusEnum = ShopEnum.getStatusByCode(status);
        shop.setStatus(statusEnum);
        boolean result = shopService.updateShop(shop);
        return ResponseEntity.ok(result);
    }

    /**
     * 推荐店铺
     */
    @PutMapping("/{shopId}/recommend")
    public ResponseEntity<Boolean> recommendShop(@PathVariable Long shopId) {
        boolean result = shopService.recommendShop(shopId);
        return ResponseEntity.ok(result);
    }

    /**
     * 取消推荐店铺
     */
    @PutMapping("/{shopId}/cancel-recommend")
    public ResponseEntity<Boolean> cancelRecommendShop(@PathVariable Long shopId) {
        boolean result = shopService.cancelRecommendShop(shopId);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 删除店铺
     */
    @DeleteMapping("/{shopId}")
    public ResponseEntity<Boolean> deleteShop(@PathVariable Long shopId) {
        Shop shop = new Shop();
        shop.setId(shopId);
        shop.setIsDeleted(1);
        boolean result = shopService.updateShop(shop);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 批量删除店铺
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Boolean> batchDeleteShops(@RequestBody List<Long> shopIds) {
        for (Long shopId : shopIds) {
            Shop shop = new Shop();
            shop.setId(shopId);
            shop.setIsDeleted(1);
            shopService.updateShop(shop);
        }
        return ResponseEntity.ok(true);
    }

    /**
     * 获取店铺列表（分页）
     */
    @GetMapping("/list")
    public ResponseEntity<Page<Shop>> getShopList(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer shopLevel,
            @RequestParam(required = false) Integer shopStatus,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        Page<Shop> shopPage = shopService.getShopList(page, pageSize, keyword, status, shopLevel, shopStatus, startTime, endTime, sortField, sortOrder);
        return ResponseEntity.ok(shopPage);
    }

    /**
     * 根据状态获取店铺列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Shop>> getShopsByStatus(@PathVariable Integer status) {
        List<Shop> shops = shopService.getShopsByStatus(status);
        return ResponseEntity.ok(shops);
    }

    /**
     * 获取推荐店铺列表
     */
    @GetMapping("/recommended")
    public ResponseEntity<List<Shop>> getRecommendedShops(@RequestParam int limit) {
        List<Shop> shops = shopService.getRecommendedShops(limit);
        return ResponseEntity.ok(shops);
    }

    /**
     * 获取店铺等级列表
     */
    @GetMapping("/level/{level}")
    public ResponseEntity<List<Shop>> getShopsByLevel(@PathVariable Integer level) {
        List<Shop> shops = shopService.getShopsByLevel(level);
        return ResponseEntity.ok(shops);
    }

    /**
     * 获取待审核店铺列表
     */
    @GetMapping("/pending-review")
    public ResponseEntity<List<Shop>> getPendingReviewShops() {
        List<Shop> shops = shopService.getShopsByAuditStatus(ShopEnum.AUDIT_PENDING); // 1 对应 AUDIT_PENDING
        return ResponseEntity.ok(shops);
    }

    // ==================== 店铺分类管理 ====================

    /**
     * 根据店铺ID获取分类列表
     */
    @GetMapping("/category/{shopId}")
    public ResponseEntity<List<ShopCategory>> getCategoriesByShopId(@PathVariable Long shopId) {
        List<ShopCategory> categories = shopCategoryService.getCategoriesByShopId(shopId);
        return ResponseEntity.ok(categories);
    }

    /**
     * 根据分类ID获取分类信息
     */
    @GetMapping("/category/detail/{categoryId}")
    public ResponseEntity<ShopCategory> getCategoryById(@PathVariable Long categoryId) {
        ShopCategory category = shopCategoryService.getCategoryById(categoryId);
        return category != null ? ResponseEntity.ok(category) : ResponseEntity.notFound().build();
    }

    /**
     * 创建分类
     */
    @PostMapping("/category")
    public ResponseEntity<Boolean> createCategory(@RequestBody ShopCategory category) {
        boolean result = shopCategoryService.createCategory(category);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新分类
     */
    @PutMapping("/category")
    public ResponseEntity<Boolean> updateCategory(@RequestBody ShopCategory category) {
        boolean result = shopCategoryService.updateCategory(category);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<Boolean> deleteCategory(@PathVariable Long categoryId) {
        boolean result = shopCategoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(result);
    }

    /**
     * 启用分类
     */
    @PutMapping("/category/{categoryId}/enable")
    public ResponseEntity<Boolean> enableCategory(@PathVariable Long categoryId) {
        boolean result = shopCategoryService.enableCategory(categoryId);
        return ResponseEntity.ok(result);
    }

    /**
     * 禁用分类
     */
    @PutMapping("/category/{categoryId}/disable")
    public ResponseEntity<Boolean> disableCategory(@PathVariable Long categoryId) {
        boolean result = shopCategoryService.disableCategory(categoryId);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据父分类ID获取子分类
     */
    @GetMapping("/category/sub/{parentId}/{shopId}")
    public ResponseEntity<List<ShopCategory>> getSubCategories(@PathVariable Long parentId, @PathVariable Long shopId) {
        List<ShopCategory> categories = shopCategoryService.getSubCategories(parentId, shopId);
        return ResponseEntity.ok(categories);
    }

    /**
     * 获取店铺的所有一级分类
     */
    @GetMapping("/category/root/{shopId}")
    public ResponseEntity<List<ShopCategory>> getRootCategories(@PathVariable Long shopId) {
        List<ShopCategory> categories = shopCategoryService.getRootCategories(shopId);
        return ResponseEntity.ok(categories);
    }

    // ==================== 店铺资质管理 ====================

    /**
     * 根据店铺ID获取资质列表
     */
    @GetMapping("/qualification/{shopId}")
    public ResponseEntity<List<ShopQualification>> getQualificationsByShopId(@PathVariable Long shopId) {
        List<ShopQualification> qualifications = shopQualificationService.getQualificationsByShopId(shopId);
        return ResponseEntity.ok(qualifications);
    }

    /**
     * 根据资质ID获取资质信息
     */
    @GetMapping("/qualification/detail/{qualificationId}")
    public ResponseEntity<ShopQualification> getQualificationById(@PathVariable Long qualificationId) {
        ShopQualification qualification = shopQualificationService.getQualificationById(qualificationId);
        return qualification != null ? ResponseEntity.ok(qualification) : ResponseEntity.notFound().build();
    }

    /**
     * 创建资质
     */
    @PostMapping("/qualification")
    public ResponseEntity<Boolean> createQualification(@RequestBody ShopQualification qualification) {
        boolean result = shopQualificationService.createQualification(qualification);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新资质
     */
    @PutMapping("/qualification")
    public ResponseEntity<Boolean> updateQualification(@RequestBody ShopQualification qualification) {
        boolean result = shopQualificationService.updateQualification(qualification);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除资质
     */
    @DeleteMapping("/qualification/{qualificationId}")
    public ResponseEntity<Boolean> deleteQualification(@PathVariable Long qualificationId) {
        boolean result = shopQualificationService.deleteQualification(qualificationId);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据资质类型获取资质列表
     */
    @GetMapping("/qualification/type/{type}/{shopId}")
    public ResponseEntity<List<ShopQualification>> getQualificationsByType(@PathVariable Integer type, @PathVariable Long shopId) {
        List<ShopQualification> qualifications = shopQualificationService.getQualificationsByType(type, shopId);
        return ResponseEntity.ok(qualifications);
    }

    /**
     * 获取即将过期的资质
     */
    @GetMapping("/qualification/expiring/{shopId}")
    public ResponseEntity<List<ShopQualification>> getExpiringQualifications(@PathVariable Long shopId, @RequestParam int days) {
        List<ShopQualification> qualifications = shopQualificationService.getExpiringQualifications(shopId, days);
        return ResponseEntity.ok(qualifications);
    }

    /**
     * 获取无效资质
     */
    @GetMapping("/qualification/invalid/{shopId}")
    public ResponseEntity<List<ShopQualification>> getInvalidQualifications(@PathVariable Long shopId) {
        List<ShopQualification> qualifications = shopQualificationService.getInvalidQualifications(shopId);
        return ResponseEntity.ok(qualifications);
    }
}

