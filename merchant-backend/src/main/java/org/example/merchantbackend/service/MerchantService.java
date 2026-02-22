package org.example.merchantbackend.service;

import org.example.merchantbackend.controller.VO.MerchantControllerVO;
import org.example.merchantbackend.entity.Merchant;

import java.util.List;

/*
 * @Author:总会落叶
 * @Date:2026/2/9
 * @Description:
 */
public interface MerchantService {

    //申请为商家
    Integer applyMerchant(Merchant merchant);

    //修改商家信息(管理员审核,商家自己修改信息)
    Integer updateMerchant(Merchant merchant,Long userId);

    //查询商家信息
    List<MerchantControllerVO> selectMerchant(Merchant merchant, Long userId);

    // 逻辑删除商家（仅管理员/测试员）
    Integer deleteMerchant(Long id, Long userId);
}
