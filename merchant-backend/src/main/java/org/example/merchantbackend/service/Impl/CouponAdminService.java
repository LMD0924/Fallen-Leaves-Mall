package org.example.merchantbackend.service.Impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.couponservice.entity.Coupon;
import org.example.couponservice.entity.CouponScope;
import org.example.couponservice.mapper.CouponMapper;
import org.example.couponservice.mapper.CouponScopeMapper;
import org.example.merchantbackend.dto.CouponAddDTO;
import org.example.merchantbackend.dto.CouponUpdateDTO;
import org.example.merchantbackend.vo.CouponDetailVO;
import org.example.merchantbackend.vo.CouponScopeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponAdminService {

    private final CouponMapper couponMapper;
    private final CouponScopeMapper couponScopeMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String COUPON_STOCK_KEY = "coupon:stock:";

    @Transactional(rollbackFor = Exception.class)
    public CouponDetailVO addCoupon(@Valid CouponAddDTO dto) {
        // 1. 创建优惠券
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(dto, coupon);

        // 设置初始值
        coupon.setStock(dto.getTotalCount());  // 初始库存 = 发行总量
        coupon.setReceiveCount(0);
        coupon.setStatus(0);  // 默认下架，需要手动上架

        couponMapper.insert(coupon);

        // 2. 保存适用范围（如果不是全场通用）
        if (dto.getScopeType() != 1 && dto.getScopeIds() != null && !dto.getScopeIds().isEmpty()) {
            List<CouponScope> scopeList = dto.getScopeIds().stream().map(scopeId -> {
                CouponScope scope = new CouponScope();
                scope.setCouponId(coupon.getId());
                scope.setScopeId(scopeId);
                scope.setScopeType(dto.getScopeType());
                return scope;
            }).collect(Collectors.toList());

            couponScopeMapper.batchInsert(scopeList);
        }

        log.info("新增优惠券成功: couponId={}, name={}", coupon.getId(), coupon.getName());

        return getCouponDetail(coupon.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public CouponDetailVO updateCoupon(CouponUpdateDTO dto) {
        Coupon coupon = couponMapper.selectById(dto.getId());
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }

        // 只能修改未上架的优惠券
        if (coupon.getStatus() == 1) {
            throw new RuntimeException("已上架的优惠券不能修改");
        }

        // 更新字段
        BeanUtils.copyProperties(dto, coupon, "id", "stock", "receiveCount");

        // 如果修改了发行总量，同步更新库存
        if (dto.getTotalCount() != null) {
            coupon.setStock(dto.getTotalCount() - coupon.getReceiveCount());
        }

        couponMapper.updateById(coupon);

        // 清除缓存
        redisTemplate.delete("coupon:detail:" + coupon.getId());

        return getCouponDetail(coupon.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCoupon(Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }

        // 只能删除未上架的优惠券
        if (coupon.getStatus() == 1) {
            throw new RuntimeException("已上架的优惠券不能删除");
        }

        // 删除适用范围
        couponScopeMapper.deleteByCouponId(couponId);

        // 删除优惠券
        couponMapper.deleteById(couponId);

        // 清除缓存
        redisTemplate.delete(COUPON_STOCK_KEY + couponId);
        redisTemplate.delete("coupon:detail:" + couponId);

        log.info("删除优惠券成功: couponId={}", couponId);
        return true;
    }

    public boolean updateCouponStatus(Long couponId, Integer status) {
        Coupon coupon = new Coupon();
        coupon.setId(couponId);
        coupon.setStatus(status);

        int updated = couponMapper.updateById(coupon);

        if (updated > 0 && status == 1) {
            // 上架时预热库存到Redis
            Coupon fullCoupon = couponMapper.selectById(couponId);
            redisTemplate.opsForValue().set(COUPON_STOCK_KEY + couponId, fullCoupon.getStock());
        }

        return updated > 0;
    }

    private CouponDetailVO getCouponDetail(Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            return null;
        }

        CouponDetailVO vo = new CouponDetailVO();
        BeanUtils.copyProperties(coupon, vo);

        // 查询适用范围
        List<CouponScope> scopes = couponScopeMapper.selectByCouponId(couponId);

        // 转换为VO列表
        List<CouponScopeVO> scopeVOs = scopes.stream().map(scope -> {
            CouponScopeVO scopeVO = new CouponScopeVO();
            BeanUtils.copyProperties(scope, scopeVO);

            // 这里可以调用商品服务获取名称（如果有需要）
            // if (scope.getScopeType() == 2) {
            //     Product product = productFeignClient.getProduct(scope.getScopeId());
            //     scopeVO.setScopeName(product.getName());
            //     scopeVO.setScopeImage(product.getMainImage());
            // }

            return scopeVO;
        }).collect(Collectors.toList());

        vo.setScopes(scopeVOs);

        return vo;
    }
}