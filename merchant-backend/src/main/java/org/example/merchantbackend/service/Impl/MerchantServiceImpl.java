package org.example.merchantbackend.service.Impl;

import org.example.backend.entity.User;
import org.example.backend.mapper.UserMapper;
import org.example.merchantbackend.entity.Merchant;
import org.example.merchantbackend.mapper.MerchantMapper;
import org.example.merchantbackend.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/*
 * @Author:总会落叶
 * @Date:2026/2/9
 * @Description:
 */
@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private UserMapper userMapper;

    /*
    * 申请为商家
    * */
    @Override
    @Transactional
    public Integer applyMerchant(Merchant merchant) {
        // 1. 校验参数：用户ID不能为空
        if (merchant == null || merchant.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        // 2. 校验用户是否存在
        User user = userMapper.selectUserById(merchant.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在，无法申请成为商家");
        }

        // 4. 校验用户是否已成为商家
        List<Merchant> merchantList = merchantMapper.selectMerchants(merchant);
        // 注意：List判断是否为空要用 !isEmpty()，而非 !=null（空List也是非null）
        if (merchantList != null && !merchantList.isEmpty()) {
            throw new RuntimeException("该用户已申请成为商家，无需重复申请");
        }

        // 5. 核心逻辑：插入商家申请数据（补充商家默认状态：待审核）
        merchant.setStatus(0); // 比如：0->待审核；1->已通过；2->已拒绝；3->已禁用
        merchantMapper.applyMerchant(merchant); // 需在MerchantMapper中添加insert方法

        // 6. 返回成功提示
        return 1;
    }

    /*
    * 修改商家信息
    * */
    @Override
    @Transactional
    public Integer updateMerchant(Merchant merchant,Long userId) {
        //先验证有无权限修改
        User user = userMapper.selectUserById(userId);
        if(!Objects.equals(user.getRole(), "管理员") && !Objects.equals(user.getRole(), "测试员") && !Objects.equals(user.getRole(), "商家")){
            throw new RuntimeException("无权限修改");
        }
        merchantMapper.updateMerchant(merchant);
        return 1;
    }

    @Override
    public List<Merchant> selectMerchant(Merchant merchant,Long userId) {
        //先验证是否有权限查询
        User user = userMapper.selectUserById(userId);
        if(!Objects.equals(user.getRole(), "管理员") && !Objects.equals(user.getRole(), "测试员") && !Objects.equals(user.getRole(), "商家")){
            throw new RuntimeException("无权限查询");
        }
        return merchantMapper.selectMerchants(merchant);
    }


    //查询申请进度
    @Override
    public Merchant queryApplyProgress(Long userId) {
        Merchant merchant = new Merchant();
        merchant.setUserId(userId);
        List<Merchant> merchantList = merchantMapper.selectMerchants(merchant);
        return merchantList.get(0);
    }

}