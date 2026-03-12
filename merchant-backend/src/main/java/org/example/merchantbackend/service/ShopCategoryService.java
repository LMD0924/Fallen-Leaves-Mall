package org.example.merchantbackend.service;

import org.example.merchantbackend.entity.ShopCategory;

import java.util.List;

/*
 * @Author:总会落叶
 * @Date:2026/3/4
 * @Description: 店铺分类服务
 */
public interface ShopCategoryService {
    /**
     * 根据店铺ID获取分类列表
     */
    List<ShopCategory> getCategoriesByShopId(Long shopId);

    /**
     * 根据分类ID获取分类信息
     */
    ShopCategory getCategoryById(Long categoryId);

    /**
     * 创建分类
     */
    boolean createCategory(ShopCategory category);

    /**
     * 更新分类
     */
    boolean updateCategory(ShopCategory category);

    /**
     * 删除分类
     */
    boolean deleteCategory(Long categoryId);

    /**
     * 启用分类
     */
    boolean enableCategory(Long categoryId);

    /**
     * 禁用分类
     */
    boolean disableCategory(Long categoryId);

    /**
     * 根据父分类ID获取子分类
     */
    List<ShopCategory> getSubCategories(Long parentId, Long shopId);

    /**
     * 获取店铺的所有一级分类
     */
    List<ShopCategory> getRootCategories(Long shopId);
}
