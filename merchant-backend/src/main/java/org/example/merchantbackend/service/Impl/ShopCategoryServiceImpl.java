package org.example.merchantbackend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.merchantbackend.entity.ShopCategory;
import org.example.merchantbackend.mapper.ShopCategoryMapper;
import org.example.merchantbackend.service.ShopCategoryService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 * @Author:总会落叶
 * @Date:2026/3/4
 * @Description: 店铺分类服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShopCategoryServiceImpl implements ShopCategoryService {

    private final ShopCategoryMapper shopCategoryMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    // Redis键前缀
    private static final String CATEGORY_KEY_PREFIX = "shop:category:";
    private static final long CACHE_EXPIRE_TIME = 30; // 缓存过期时间（分钟）

    @Override
    public List<ShopCategory> getCategoriesByShopId(Long shopId) {
        String key = CATEGORY_KEY_PREFIX + "shop:" + shopId;
        // 尝试从缓存获取
        List<ShopCategory> categories = (List<ShopCategory>) redisTemplate.opsForValue().get(key);
        if (categories != null) {
            return categories;
        }
        // 缓存未命中，从数据库查询
        QueryWrapper<ShopCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("shop_id", shopId)
                .eq("is_deleted", 0)
                .orderByAsc("sort");
        categories = shopCategoryMapper.selectList(wrapper);
        // 存入缓存
        redisTemplate.opsForValue().set(key, categories, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return categories;
    }

    @Override
    public ShopCategory getCategoryById(Long categoryId) {
        String key = CATEGORY_KEY_PREFIX + "id:" + categoryId;
        // 尝试从缓存获取
        ShopCategory category = (ShopCategory) redisTemplate.opsForValue().get(key);
        if (category != null) {
            return category;
        }
        // 缓存未命中，从数据库查询
        QueryWrapper<ShopCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("id", categoryId)
                .eq("is_deleted", 0);
        category = shopCategoryMapper.selectOne(wrapper);
        // 存入缓存
        if (category != null) {
            redisTemplate.opsForValue().set(key, category, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        }
        return category;
    }

    @Override
    public boolean createCategory(ShopCategory category) {
        category.setIsDeleted(0);
        category.setStatus(1); // 默认启用
        if (category.getSort() == null) {
            category.setSort(0);
        }
        boolean result = shopCategoryMapper.insert(category) > 0;
        if (result) {
            // 清除相关缓存
            redisTemplate.delete(CATEGORY_KEY_PREFIX + "shop:" + category.getShopId());
            if (category.getParentId() != null) {
                redisTemplate.delete(CATEGORY_KEY_PREFIX + "sub:" + category.getParentId() + ":" + category.getShopId());
            }
            if (category.getParentId() != null && category.getParentId() == 0) {
                redisTemplate.delete(CATEGORY_KEY_PREFIX + "root:" + category.getShopId());
            }
        }
        return result;
    }

    @Override
    public boolean updateCategory(ShopCategory category) {
        boolean result = shopCategoryMapper.updateById(category) > 0;
        if (result) {
            // 清除相关缓存
            redisTemplate.delete(CATEGORY_KEY_PREFIX + "id:" + category.getId());
            redisTemplate.delete(CATEGORY_KEY_PREFIX + "shop:" + category.getShopId());
            if (category.getParentId() != null) {
                redisTemplate.delete(CATEGORY_KEY_PREFIX + "sub:" + category.getParentId() + ":" + category.getShopId());
            }
            if (category.getParentId() != null && category.getParentId() == 0) {
                redisTemplate.delete(CATEGORY_KEY_PREFIX + "root:" + category.getShopId());
            }
        }
        return result;
    }

    @Override
    public boolean deleteCategory(Long categoryId) {
        // 先获取分类信息，用于清除缓存
        ShopCategory category = getCategoryById(categoryId);
        if (category == null) {
            return false;
        }
        category.setIsDeleted(1); // 软删除
        boolean result = shopCategoryMapper.updateById(category) > 0;
        if (result) {
            // 清除相关缓存
            redisTemplate.delete(CATEGORY_KEY_PREFIX + "id:" + categoryId);
            redisTemplate.delete(CATEGORY_KEY_PREFIX + "shop:" + category.getShopId());
            if (category.getParentId() != null) {
                redisTemplate.delete(CATEGORY_KEY_PREFIX + "sub:" + category.getParentId() + ":" + category.getShopId());
            }
            if (category.getParentId() != null && category.getParentId() == 0) {
                redisTemplate.delete(CATEGORY_KEY_PREFIX + "root:" + category.getShopId());
            }
        }
        return result;
    }

    @Override
    public boolean enableCategory(Long categoryId) {
        // 先获取分类信息，用于清除缓存
        ShopCategory category = getCategoryById(categoryId);
        if (category == null) {
            return false;
        }
        category.setStatus(1); // 启用
        boolean result = shopCategoryMapper.updateById(category) > 0;
        if (result) {
            // 清除相关缓存
            redisTemplate.delete(CATEGORY_KEY_PREFIX + "id:" + categoryId);
            redisTemplate.delete(CATEGORY_KEY_PREFIX + "shop:" + category.getShopId());
            if (category.getParentId() != null) {
                redisTemplate.delete(CATEGORY_KEY_PREFIX + "sub:" + category.getParentId() + ":" + category.getShopId());
            }
            if (category.getParentId() != null && category.getParentId() == 0) {
                redisTemplate.delete(CATEGORY_KEY_PREFIX + "root:" + category.getShopId());
            }
        }
        return result;
    }

    @Override
    public boolean disableCategory(Long categoryId) {
        // 先获取分类信息，用于清除缓存
        ShopCategory category = getCategoryById(categoryId);
        if (category == null) {
            return false;
        }
        category.setStatus(0); // 禁用
        boolean result = shopCategoryMapper.updateById(category) > 0;
        if (result) {
            // 清除相关缓存
            redisTemplate.delete(CATEGORY_KEY_PREFIX + "id:" + categoryId);
            redisTemplate.delete(CATEGORY_KEY_PREFIX + "shop:" + category.getShopId());
            if (category.getParentId() != null) {
                redisTemplate.delete(CATEGORY_KEY_PREFIX + "sub:" + category.getParentId() + ":" + category.getShopId());
            }
            if (category.getParentId() != null && category.getParentId() == 0) {
                redisTemplate.delete(CATEGORY_KEY_PREFIX + "root:" + category.getShopId());
            }
        }
        return result;
    }

    @Override
    public List<ShopCategory> getSubCategories(Long parentId, Long shopId) {
        String key = CATEGORY_KEY_PREFIX + "sub:" + parentId + ":" + shopId;
        // 尝试从缓存获取
        List<ShopCategory> categories = (List<ShopCategory>) redisTemplate.opsForValue().get(key);
        if (categories != null) {
            return categories;
        }
        // 缓存未命中，从数据库查询
        QueryWrapper<ShopCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", parentId)
                .eq("shop_id", shopId)
                .eq("is_deleted", 0)
                .orderByAsc("sort");
        categories = shopCategoryMapper.selectList(wrapper);
        // 存入缓存
        redisTemplate.opsForValue().set(key, categories, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return categories;
    }

    @Override
    public List<ShopCategory> getRootCategories(Long shopId) {
        String key = CATEGORY_KEY_PREFIX + "root:" + shopId;
        // 尝试从缓存获取
        List<ShopCategory> categories = (List<ShopCategory>) redisTemplate.opsForValue().get(key);
        if (categories != null) {
            return categories;
        }
        // 缓存未命中，从数据库查询
        QueryWrapper<ShopCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", 0)
                .eq("shop_id", shopId)
                .eq("is_deleted", 0)
                .orderByAsc("sort");
        categories = shopCategoryMapper.selectList(wrapper);
        // 存入缓存
        redisTemplate.opsForValue().set(key, categories, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return categories;
    }
}
