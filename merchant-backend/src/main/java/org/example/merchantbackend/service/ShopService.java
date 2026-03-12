package org.example.merchantbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.commonbackend.code.ShopEnum;
import org.example.merchantbackend.entity.Shop;

import java.util.List;

/*
 * @Author:总会落叶
 * @Date:2026/2/20
 * @Description:
 */
public interface ShopService {

    /**
     * 管理员审核店铺
     */
    boolean adminReviewShop(Integer id, ShopEnum shopStatus, String rejectReason);


    /**
     * 商家申请店铺
     */
    boolean applyShop(Shop shop);

    /**
     * 根据商家ID获取店铺信息
     */
    Shop getShopByMerchantId(Long merchantId);

    /**
     * 根据店铺ID获取店铺信息
     */
    Shop getShopById(Long shopId);

    /**
     * 创建店铺
     */
    boolean createShop(Shop shop);

    /**
     * 更新店铺信息
     */
    boolean updateShop(Shop shop);

    /**
     * 暂停店铺
     */
    boolean pauseShop(Long shopId);

    /**
     * 恢复店铺
     */
    boolean resumeShop(Long shopId);

    /**
     * 关闭店铺
     */
    boolean closeShop(Long shopId);

    /**
     * 推荐店铺
     */
    boolean recommendShop(Long shopId);

    /**
     * 取消推荐店铺
     */
    boolean cancelRecommendShop(Long shopId);

    /**
     * 获取店铺列表（分页）
     */
    Page<Shop> getShopList(int page, int pageSize);
    
    /**
     * 获取店铺列表（带筛选条件）
     */
    Page<Shop> getShopList(int page, int pageSize, String keyword, Integer status, Integer shopLevel, Integer shopStatus, String startTime, String endTime, String sortField, String sortOrder);

    /**
     * 根据状态获取店铺列表
     */
    List<Shop> getShopsByStatus(Integer status);

    /**
     * 获取推荐店铺列表
     */
    List<Shop> getRecommendedShops(int limit);

    /**
     * 获取店铺等级列表
     */
    List<Shop> getShopsByLevel(Integer level);

    /**
     * 根据审核状态获取店铺列表
     */
    List<Shop> getShopsByAuditStatus(ShopEnum shopStatus);
}

