package org.example.merchantbackend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.merchantbackend.entity.ShopQualification;
import org.example.merchantbackend.mapper.ShopQualificationMapper;
import org.example.merchantbackend.service.ShopQualificationService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 * @Author:总会落叶
 * @Date:2026/3/4
 * @Description: 店铺资质服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShopQualificationServiceImpl implements ShopQualificationService {

    private final ShopQualificationMapper shopQualificationMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    // Redis键前缀
    private static final String QUALIFICATION_KEY_PREFIX = "shop:qualification:";
    private static final long CACHE_EXPIRE_TIME = 30; // 缓存过期时间（分钟）

    @Override
    public List<ShopQualification> getQualificationsByShopId(Long shopId) {
        String key = QUALIFICATION_KEY_PREFIX + "shop:" + shopId;
        // 尝试从缓存获取
        List<ShopQualification> qualifications = (List<ShopQualification>) redisTemplate.opsForValue().get(key);
        if (qualifications != null) {
            return qualifications;
        }
        // 缓存未命中，从数据库查询
        QueryWrapper<ShopQualification> wrapper = new QueryWrapper<>();
        wrapper.eq("shop_id", shopId)
                .orderByDesc("create_time");
        qualifications = shopQualificationMapper.selectList(wrapper);
        // 存入缓存
        redisTemplate.opsForValue().set(key, qualifications, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return qualifications;
    }

    @Override
    public ShopQualification getQualificationById(Long qualificationId) {
        String key = QUALIFICATION_KEY_PREFIX + "id:" + qualificationId;
        // 尝试从缓存获取
        ShopQualification qualification = (ShopQualification) redisTemplate.opsForValue().get(key);
        if (qualification != null) {
            return qualification;
        }
        // 缓存未命中，从数据库查询
        qualification = shopQualificationMapper.selectById(qualificationId);
        // 存入缓存
        if (qualification != null) {
            redisTemplate.opsForValue().set(key, qualification, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        }
        return qualification;
    }

    @Override
    public boolean createQualification(ShopQualification qualification) {
        qualification.setStatus(1); // 默认有效
        boolean result = shopQualificationMapper.insert(qualification) > 0;
        if (result) {
            // 清除相关缓存
            redisTemplate.delete(QUALIFICATION_KEY_PREFIX + "shop:" + qualification.getShopId());
            redisTemplate.delete(QUALIFICATION_KEY_PREFIX + "type:" + qualification.getQualificationType() + ":" + qualification.getShopId());
            redisTemplate.delete(QUALIFICATION_KEY_PREFIX + "expiring:" + qualification.getShopId());
            redisTemplate.delete(QUALIFICATION_KEY_PREFIX + "invalid:" + qualification.getShopId());
        }
        return result;
    }

    @Override
    public boolean updateQualification(ShopQualification qualification) {
        boolean result = shopQualificationMapper.updateById(qualification) > 0;
        if (result) {
            // 清除相关缓存
            redisTemplate.delete(QUALIFICATION_KEY_PREFIX + "id:" + qualification.getId());
            redisTemplate.delete(QUALIFICATION_KEY_PREFIX + "shop:" + qualification.getShopId());
            redisTemplate.delete(QUALIFICATION_KEY_PREFIX + "type:" + qualification.getQualificationType() + ":" + qualification.getShopId());
            redisTemplate.delete(QUALIFICATION_KEY_PREFIX + "expiring:" + qualification.getShopId());
            redisTemplate.delete(QUALIFICATION_KEY_PREFIX + "invalid:" + qualification.getShopId());
        }
        return result;
    }

    @Override
    public boolean deleteQualification(Long qualificationId) {
        // 先获取资质信息，用于清除缓存
        ShopQualification qualification = getQualificationById(qualificationId);
        if (qualification == null) {
            return false;
        }
        boolean result = shopQualificationMapper.deleteById(qualificationId) > 0;
        if (result) {
            // 清除相关缓存
            redisTemplate.delete(QUALIFICATION_KEY_PREFIX + "id:" + qualificationId);
            redisTemplate.delete(QUALIFICATION_KEY_PREFIX + "shop:" + qualification.getShopId());
            redisTemplate.delete(QUALIFICATION_KEY_PREFIX + "type:" + qualification.getQualificationType() + ":" + qualification.getShopId());
            redisTemplate.delete(QUALIFICATION_KEY_PREFIX + "expiring:" + qualification.getShopId());
            redisTemplate.delete(QUALIFICATION_KEY_PREFIX + "invalid:" + qualification.getShopId());
        }
        return result;
    }

    @Override
    public List<ShopQualification> getQualificationsByType(Integer type, Long shopId) {
        String key = QUALIFICATION_KEY_PREFIX + "type:" + type + ":" + shopId;
        // 尝试从缓存获取
        List<ShopQualification> qualifications = (List<ShopQualification>) redisTemplate.opsForValue().get(key);
        if (qualifications != null) {
            return qualifications;
        }
        // 缓存未命中，从数据库查询
        QueryWrapper<ShopQualification> wrapper = new QueryWrapper<>();
        wrapper.eq("qualification_type", type)
                .eq("shop_id", shopId)
                .orderByDesc("create_time");
        qualifications = shopQualificationMapper.selectList(wrapper);
        // 存入缓存
        redisTemplate.opsForValue().set(key, qualifications, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return qualifications;
    }

    @Override
    public List<ShopQualification> getExpiringQualifications(Long shopId, int days) {
        String key = QUALIFICATION_KEY_PREFIX + "expiring:" + shopId + ":" + days;
        // 尝试从缓存获取
        List<ShopQualification> qualifications = (List<ShopQualification>) redisTemplate.opsForValue().get(key);
        if (qualifications != null) {
            return qualifications;
        }
        // 缓存未命中，从数据库查询
        LocalDate now = LocalDate.now();
        LocalDate futureDate = now.plusDays(days);
        
        QueryWrapper<ShopQualification> wrapper = new QueryWrapper<>();
        wrapper.eq("shop_id", shopId)
                .ge("expire_date", now)
                .le("expire_date", futureDate)
                .eq("status", 1)
                .orderByAsc("expire_date");
        qualifications = shopQualificationMapper.selectList(wrapper);
        // 存入缓存
        redisTemplate.opsForValue().set(key, qualifications, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return qualifications;
    }

    @Override
    public List<ShopQualification> getInvalidQualifications(Long shopId) {
        String key = QUALIFICATION_KEY_PREFIX + "invalid:" + shopId;
        // 尝试从缓存获取
        List<ShopQualification> qualifications = (List<ShopQualification>) redisTemplate.opsForValue().get(key);
        if (qualifications != null) {
            return qualifications;
        }
        // 缓存未命中，从数据库查询
        LocalDate now = LocalDate.now();
        
        QueryWrapper<ShopQualification> wrapper = new QueryWrapper<>();
        wrapper.eq("shop_id", shopId)
                .and(w -> w.eq("status", 0).or().lt("expire_date", now))
                .orderByDesc("create_time");
        qualifications = shopQualificationMapper.selectList(wrapper);
        // 存入缓存
        redisTemplate.opsForValue().set(key, qualifications, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return qualifications;
    }
}
