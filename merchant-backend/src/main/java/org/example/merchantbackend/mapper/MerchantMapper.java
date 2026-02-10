package org.example.merchantbackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.merchantbackend.entity.Merchant;

import java.util.List;
import java.util.Map;

/*
 * @Author:总会落叶
 * @Date:2026/2/9
 * @Description:
 */
@Mapper
public interface MerchantMapper {

    //申请为商家
    Integer applyMerchant(Merchant merchant);

    //修改商家信息(管理员审核,商家自己修改信息)
    String updateMerchant(Merchant merchant);

    //查询商家信息
    List<Merchant> selectMerchants(Merchant merchant);

}
