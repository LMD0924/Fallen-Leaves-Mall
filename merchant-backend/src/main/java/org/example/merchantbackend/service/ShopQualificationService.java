package org.example.merchantbackend.service;

import org.example.merchantbackend.entity.ShopQualification;

import java.util.List;

/*
 * @Author:总会落叶
 * @Date:2026/3/4
 * @Description: 店铺资质服务
 */
public interface ShopQualificationService {
    /**
     * 根据店铺ID获取资质列表
     */
    List<ShopQualification> getQualificationsByShopId(Long shopId);

    /**
     * 根据资质ID获取资质信息
     */
    ShopQualification getQualificationById(Long qualificationId);

    /**
     * 创建资质
     */
    boolean createQualification(ShopQualification qualification);

    /**
     * 更新资质
     */
    boolean updateQualification(ShopQualification qualification);

    /**
     * 删除资质
     */
    boolean deleteQualification(Long qualificationId);

    /**
     * 根据资质类型获取资质列表
     */
    List<ShopQualification> getQualificationsByType(Integer type, Long shopId);

    /**
     * 获取即将过期的资质
     */
    List<ShopQualification> getExpiringQualifications(Long shopId, int days);

    /**
     * 获取无效资质
     */
    List<ShopQualification> getInvalidQualifications(Long shopId);
}
