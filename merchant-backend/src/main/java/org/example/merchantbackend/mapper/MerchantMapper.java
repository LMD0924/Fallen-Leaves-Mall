package org.example.merchantbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.example.commonbackend.code.MerchantStatus;
import org.example.merchantbackend.entity.Merchant;
import org.example.merchantbackend.entity.dto.AuditMerchantDTO;

/*
 * @Author:总会落叶
 * @Date:2026/2/9
 * @Description: 商家 Mapper（MyBatis-Plus）
 */
@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {

    //管理员审核
    @Update("update merchant set status=#{status},reject_reason=#{rejectReason} where id=#{id}")
    int adminAuditMerchant(AuditMerchantDTO dto);
}
