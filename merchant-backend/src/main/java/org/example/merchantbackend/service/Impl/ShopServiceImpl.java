package org.example.merchantbackend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.entity.User;
import org.example.backend.mapper.UserMapper;
import org.example.commonbackend.code.ShopEnum;
import org.example.merchantbackend.entity.Shop;
import org.example.merchantbackend.mapper.ShopMapper;
import org.example.merchantbackend.service.ShopService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/*
 * @Author:总会落叶
 * @Date:2026/2/20
 * @Description:
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopMapper shopMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserMapper userMapper;

    // Redis键前缀
    private static final String SHOP_KEY_PREFIX = "shop:";
    private static final String SHOP_LIST_KEY_PREFIX = "shop:list:";
    private static final String SHOP_RECOMMENDED_KEY = "shop:recommended";
    private static final long CACHE_EXPIRE_TIME = 30; // 缓存过期时间（分钟）


    //管理员审核店铺
    @Override
    public boolean adminReviewShop(Integer id, ShopEnum shopStatus, String rejectReason) {
        Shop shop = shopMapper.selectById(id);
        if (shop == null) {
            return false;
        }
        shop.setShopStatus(shopStatus);
        if(shopStatus == ShopEnum.AUDIT_REJECTED){
            shop.setRejectReason(rejectReason);
        }
        boolean result = shopMapper.updateById(shop) > 0;
        if (result) {
            // 清除相关缓存
            redisTemplate.delete(SHOP_KEY_PREFIX + "id:" + id);
            redisTemplate.delete(SHOP_KEY_PREFIX + "merchant:" + shop.getMerchantId());
            redisTemplate.delete(SHOP_LIST_KEY_PREFIX + "all");
            redisTemplate.delete(SHOP_RECOMMENDED_KEY);
        }
        return result;
    }

    //商家申请店铺
    @Override
    public boolean applyShop(Shop shop) {
        Long merchantId = shop.getMerchantId();
        User user = userMapper.selectById(merchantId);
        if(!Objects.equals(user.getRole(), "商家")) return false;
        shop.setShopLevel(ShopEnum.LEVEL_COMMON);
        shop.setStatus(ShopEnum.STATUS_NORMAL);
        shop.setIsDeleted(0);
        shop.setShopStatus(ShopEnum.AUDIT_PENDING);
        return shopMapper.insert(shop) > 0;
    }


    @Override
    public Shop getShopByMerchantId(Long merchantId) {
        String key = SHOP_KEY_PREFIX + "merchant:" + merchantId;
        // 尝试从缓存获取
        Shop shop = (Shop) redisTemplate.opsForValue().get(key);
        if (shop != null) {
            return shop;
        }
        // 缓存未命中，从数据库查询
        QueryWrapper<Shop> wrapper = new QueryWrapper<>();
        wrapper.eq("merchant_id", merchantId)
                .eq("is_deleted", 0);
        shop = shopMapper.selectOne(wrapper);
        // 存入缓存
        if (shop != null) {
            redisTemplate.opsForValue().set(key, shop, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        }
        return shop;
    }

    @Override
    public Shop getShopById(Long shopId) {
        String key = SHOP_KEY_PREFIX + "id:" + shopId;
        // 尝试从缓存获取
        Shop shop = (Shop) redisTemplate.opsForValue().get(key);
        if (shop != null) {
            return shop;
        }
        // 缓存未命中，从数据库查询
        QueryWrapper<Shop> wrapper = new QueryWrapper<>();
        wrapper.eq("id", shopId)
                .eq("is_deleted", 0);
        shop = shopMapper.selectOne(wrapper);
        // 存入缓存
        if (shop != null) {
            redisTemplate.opsForValue().set(key, shop, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        }
        return shop;
    }

    @Override
    public boolean createShop(Shop shop) {
        shop.setIsDeleted(0);
        shop.setStatus(ShopEnum.STATUS_NORMAL); // 默认正常状态
        shop.setIsRecommend(0); // 默认不推荐
        shop.setIsVerified(0); // 默认未认证
        boolean result = shopMapper.insert(shop) > 0;
        if (result) {
            // 清除相关缓存
            redisTemplate.delete(SHOP_LIST_KEY_PREFIX + "all");
            redisTemplate.delete(SHOP_RECOMMENDED_KEY);
        }
        return result;
    }

    @Override
    public boolean updateShop(Shop shop) {
        boolean result = shopMapper.updateById(shop) > 0;
        if (result) {
            // 清除相关缓存
            redisTemplate.delete(SHOP_KEY_PREFIX + "id:" + shop.getId());
            redisTemplate.delete(SHOP_KEY_PREFIX + "merchant:" + shop.getMerchantId());
            redisTemplate.delete(SHOP_LIST_KEY_PREFIX + "all");
            redisTemplate.delete(SHOP_RECOMMENDED_KEY);
        }
        return result;
    }

    @Override
    public boolean pauseShop(Long shopId) {
        Shop shop = new Shop();
        shop.setId(shopId);
        shop.setStatus(ShopEnum.STATUS_REST); // 休息中
        boolean result = shopMapper.updateById(shop) > 0;
        if (result) {
            // 清除相关缓存
            redisTemplate.delete(SHOP_KEY_PREFIX + "id:" + shopId);
            redisTemplate.delete(SHOP_LIST_KEY_PREFIX + "all");
            redisTemplate.delete(SHOP_RECOMMENDED_KEY);
        }
        return result;
    }

    @Override
    public boolean resumeShop(Long shopId) {
        Shop shop = new Shop();
        shop.setId(shopId);
        shop.setStatus(ShopEnum.STATUS_NORMAL); // 正常
        boolean result = shopMapper.updateById(shop) > 0;
        if (result) {
            // 清除相关缓存
            redisTemplate.delete(SHOP_KEY_PREFIX + "id:" + shopId);
            redisTemplate.delete(SHOP_LIST_KEY_PREFIX + "all");
            redisTemplate.delete(SHOP_RECOMMENDED_KEY);
        }
        return result;
    }

    @Override
    public boolean closeShop(Long shopId) {
        Shop shop = new Shop();
        shop.setId(shopId);
        shop.setStatus(ShopEnum.STATUS_CLOSED); // 已关闭
        boolean result = shopMapper.updateById(shop) > 0;
        if (result) {
            // 清除相关缓存
            redisTemplate.delete(SHOP_KEY_PREFIX + "id:" + shopId);
            redisTemplate.delete(SHOP_LIST_KEY_PREFIX + "all");
            redisTemplate.delete(SHOP_RECOMMENDED_KEY);
        }
        return result;
    }

    @Override
    public boolean recommendShop(Long shopId) {
        Shop shop = new Shop();
        shop.setId(shopId);
        shop.setIsRecommend(1); // 推荐
        boolean result = shopMapper.updateById(shop) > 0;
        if (result) {
            // 清除相关缓存
            redisTemplate.delete(SHOP_KEY_PREFIX + "id:" + shopId);
            redisTemplate.delete(SHOP_LIST_KEY_PREFIX + "all");
            redisTemplate.delete(SHOP_RECOMMENDED_KEY);
        }
        return result;
    }

    @Override
    public boolean cancelRecommendShop(Long shopId) {
        Shop shop = new Shop();
        shop.setId(shopId);
        shop.setIsRecommend(0); // 取消推荐
        boolean result = shopMapper.updateById(shop) > 0;
        if (result) {
            // 清除相关缓存
            redisTemplate.delete(SHOP_KEY_PREFIX + "id:" + shopId);
            redisTemplate.delete(SHOP_LIST_KEY_PREFIX + "all");
            redisTemplate.delete(SHOP_RECOMMENDED_KEY);
        }
        return result;
    }

    @Override
    public Page<Shop> getShopList(int page, int pageSize) {
        String key = SHOP_LIST_KEY_PREFIX + "all:" + page + ":" + pageSize;
        // 尝试从缓存获取
        Page<Shop> shopPage = (Page<Shop>) redisTemplate.opsForValue().get(key);
        if (shopPage != null) {
            return shopPage;
        }
        // 缓存未命中，从数据库查询
        shopPage = new Page<>(page, pageSize);
        QueryWrapper<Shop> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0)
                .orderByDesc("create_time");
        shopPage = shopMapper.selectPage(shopPage, wrapper);
        // 存入缓存
        redisTemplate.opsForValue().set(key, shopPage, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return shopPage;
    }

    @Override
    public Page<Shop> getShopList(int page, int pageSize, String keyword, Integer status, Integer shopLevel, Integer shopStatus, String startTime, String endTime, String sortField, String sortOrder) {
        // 构建查询条件
        QueryWrapper<Shop> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0);
        
        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("shop_name", keyword)
                   .or().like("merchant_id", keyword)
                   .or().like("contact_wechat", keyword)
                   .or().like("contact_qq", keyword);
        }
        
        // 店铺状态
        if (status != null) {
            wrapper.eq("status", status);
        }
        
        // 店铺等级
        if (shopLevel != null) {
            wrapper.eq("shop_level", shopLevel);
        }
        
        // 审核状态
        if (shopStatus != null) {
            wrapper.eq("shop_status", shopStatus);
        }
        
        // 时间范围
        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge("create_time", startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le("create_time", endTime);
        }
        
        // 排序
        if (sortField != null && !sortField.isEmpty()) {
            if ("descending".equals(sortOrder)) {
                wrapper.orderByDesc(sortField);
            } else {
                wrapper.orderByAsc(sortField);
            }
        } else {
            wrapper.orderByDesc("create_time");
        }
        
        // 执行查询
        Page<Shop> shopPage = new Page<>(page, pageSize);
        shopPage = shopMapper.selectPage(shopPage, wrapper);
        
        return shopPage;
    }

    @Override
    public List<Shop> getShopsByStatus(Integer status) {
        String key = SHOP_LIST_KEY_PREFIX + "status:" + status;
        // 尝试从缓存获取
        List<Shop> shops = (List<Shop>) redisTemplate.opsForValue().get(key);
        if (shops != null) {
            return shops;
        }
        // 缓存未命中，从数据库查询
        QueryWrapper<Shop> wrapper = new QueryWrapper<>();
        wrapper.eq("status", status)
                .eq("is_deleted", 0);
        shops = shopMapper.selectList(wrapper);
        // 存入缓存
        redisTemplate.opsForValue().set(key, shops, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return shops;
    }

    @Override
    public List<Shop> getRecommendedShops(int limit) {
        String key = SHOP_RECOMMENDED_KEY + ":" + limit;
        // 尝试从缓存获取
        List<Shop> shops = (List<Shop>) redisTemplate.opsForValue().get(key);
        if (shops != null) {
            return shops;
        }
        // 缓存未命中，从数据库查询
        QueryWrapper<Shop> wrapper = new QueryWrapper<>();
        wrapper.eq("is_recommend", 1)
                .eq("is_deleted", 0)
                .eq("status", 1)
                .orderByDesc("shop_score")
                .last("LIMIT " + limit);
        shops = shopMapper.selectList(wrapper);
        // 存入缓存
        redisTemplate.opsForValue().set(key, shops, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return shops;
    }

    @Override
    public List<Shop> getShopsByLevel(Integer level) {
        String key = SHOP_LIST_KEY_PREFIX + "level:" + level;
        // 尝试从缓存获取
        List<Shop> shops = (List<Shop>) redisTemplate.opsForValue().get(key);
        if (shops != null) {
            return shops;
        }
        // 缓存未命中，从数据库查询
        QueryWrapper<Shop> wrapper = new QueryWrapper<>();
        wrapper.eq("shop_level", level)
                .eq("is_deleted", 0);
        shops = shopMapper.selectList(wrapper);
        // 存入缓存
        redisTemplate.opsForValue().set(key, shops, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return shops;
    }

    @Override
    public List<Shop> getShopsByAuditStatus(ShopEnum shopStatus) {
        String key = SHOP_LIST_KEY_PREFIX + "audit:" + shopStatus;
        // 尝试从缓存获取
        List<Shop> shops = (List<Shop>) redisTemplate.opsForValue().get(key);
        if (shops != null) {
            return shops;
        }
        // 缓存未命中，从数据库查询
        QueryWrapper<Shop> wrapper = new QueryWrapper<>();
        wrapper.eq("shop_status", shopStatus)
                .eq("is_deleted", 0);
        shops = shopMapper.selectList(wrapper);
        // 存入缓存
        redisTemplate.opsForValue().set(key, shops, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return shops;
    }
}

