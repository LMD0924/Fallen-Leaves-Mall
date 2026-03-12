package org.example.merchantbackend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.backend.common.RestBean;
import org.example.backend.controller.VO.LoginResultVO;
import org.example.backend.entity.User;
import org.example.backend.mapper.UserMapper;
import org.example.commonbackend.code.MerchantStatus;
import org.example.merchantbackend.controller.VO.MerchantControllerVO;
import org.example.merchantbackend.entity.Merchant;
import org.example.merchantbackend.entity.dto.AuditMerchantDTO;
import org.example.merchantbackend.mapper.MerchantMapper;
import org.example.merchantbackend.service.MerchantService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/*
 * @Author:总会落叶
 * @Date:2026/2/9
 * @Description: 商家服务实现（MyBatis-Plus）
 */
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper,Merchant> implements MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Integer applyMerchant(Merchant merchant) {
        if (merchant == null || merchant.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getUserId, merchant.getUserId());
        Long count = merchantMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new RuntimeException("该用户已申请成为商家，无需重复申请");
        }

        merchant.setStatus(MerchantStatus.PENDING_REVIEW); // 待审核
        merchant.setIsDeleted(0);
        int rows = merchantMapper.insert(merchant);
        return rows > 0 ? 1 : 0;
    }

    //管理员审核
    @Override
    public Integer adminAuditMerchant(AuditMerchantDTO dto) {
        int rows = merchantMapper.adminAuditMerchant(dto);
        if (rows > 0 && dto.getStatus() != null && Objects.equals(dto.getStatus(), MerchantStatus.APPROVED)) {
            User user = new User();
            user.setId(dto.getUserId());
            user.setRole("商家");
            userMapper.updateUser(user);
        }
        return rows > 0 ? 1 : 0;
    }

    @Override
    @Transactional
    public Integer updateMerchant(Merchant merchant) {
        return merchantMapper.updateById(merchant) > 0 ? 1 : 0;
    }

    @Override
    public List<MerchantControllerVO> selectMerchant(Merchant merchant, Long userId) {
        // 简化权限验证，只检查userId是否存在
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        if (merchant != null) {
            if (merchant.getId() != null) {
                wrapper.eq(Merchant::getId, merchant.getId());
            }
            if (merchant.getUserId() != null) {
                wrapper.eq(Merchant::getUserId, merchant.getUserId());
            }
            if (merchant.getMerchantType() != null) {
                wrapper.eq(Merchant::getMerchantType, merchant.getMerchantType());
            }
            if (StringUtils.hasText(merchant.getMerchantName())) {
                wrapper.like(Merchant::getMerchantName, merchant.getMerchantName());
            }
            if (merchant.getStatus() != null) {
                wrapper.eq(Merchant::getStatus, merchant.getStatus());
            }
        }
        wrapper.orderByDesc(Merchant::getCreateTime);

        List<Merchant> list = merchantMapper.selectList(wrapper);
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Integer deleteMerchant(Long id, Long userId) {
        // 简化权限验证，只检查userId是否存在
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        // 逻辑删除（MyBatis-Plus @TableLogic 会转为 update is_deleted=1）
        int rows = merchantMapper.deleteById(id);
        return rows > 0 ? 1 : 0;
    }

    private MerchantControllerVO toVO(Merchant entity) {
        MerchantControllerVO vo = new MerchantControllerVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setLicenseImage(entity.getLicenseImage());
        return vo;
    }

    //获取商家总数
    @Override
    public Long getMerchantCount(){
        return merchantMapper.selectCount(null);
    }

    @Override
    public Long getTodayMerchantCount(){
        return lambdaQuery()
                .eq(Merchant::getStatus, MerchantStatus.APPROVED)
                .ge(Merchant::getCreateTime, RestBean.getTodayStartTime())
                .le(Merchant::getCreateTime, RestBean.getTodayEndTime())
                .count();
    }
}
