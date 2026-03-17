package org.example.productservice.service.Impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.productservice.dto.DeductStockForOrderDTO;
import org.example.productservice.dto.ProductQueryDTO;
import org.example.productservice.dto.RestoreStockDTO;
import org.example.productservice.entity.Product;
import org.example.productservice.entity.ProductSku;
import org.example.productservice.mapper.ProductMapper;
import org.example.productservice.mapper.ProductSkuMapper;
import org.example.productservice.service.ProductService;
import org.example.productservice.vo.*;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductMapper productMapper;
    private final ProductSkuMapper skuMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;

    // Redis key前缀
    private static final String PRODUCT_DETAIL_KEY = "product:detail:";
    private static final String PRODUCT_LIST_KEY = "product:list:";
    private static final String PRODUCT_STOCK_KEY = "product:stock:";
    private static final String SKU_STOCK_KEY = "sku:stock:";
    private static final String SKU_INFO_KEY = "product:sku:";
    private static final String SECKILL_STOCK_KEY = "seckill:stock:";

    // Lua脚本：扣减库存
    private static final String DEDUCT_STOCK_LUA =
            "local stock_key = KEYS[1] " +
                    "local sku_keys = KEYS[2] " +
                    "local deduct_count = tonumber(ARGV[1]) " +
                    "local sku_deducts = cjson.decode(ARGV[2]) " +
                    "local stock = redis.call('get', stock_key) " +
                    "if not stock or tonumber(stock) < deduct_count then " +
                    "    return 0 " +
                    "end " +
                    "for i, sku in ipairs(sku_deducts) do " +
                    "    local sku_key = 'sku:stock:' .. sku.skuId " +
                    "    local sku_stock = redis.call('get', sku_key) " +
                    "    if not sku_stock or tonumber(sku_stock) < sku.count then " +
                    "        return 0 " +
                    "    end " +
                    "end " +
                    "redis.call('decrby', stock_key, deduct_count) " +
                    "for i, sku in ipairs(sku_deducts) do " +
                    "    local sku_key = 'sku:stock:' .. sku.skuId " +
                    "    redis.call('decrby', sku_key, sku.count) " +
                    "end " +
                    "return 1";

    @Override
    public Page<ProductListVO> getProductList(ProductQueryDTO queryDTO) {
        // 生成缓存key
        String cacheKey = PRODUCT_LIST_KEY + queryDTO.hashCode();

        // 1. 尝试从缓存获取
        Page<ProductListVO> cachePage = (Page<ProductListVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachePage != null) {
            log.info("从缓存获取商品列表: {}", cacheKey);
            return cachePage;
        }

        // 2. 缓存未命中，查询数据库
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            wrapper.like(Product::getName, queryDTO.getKeyword());
        }
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Product::getCategoryId, queryDTO.getCategoryId());
        }
        wrapper.orderByDesc(Product::getCreateTime);

        Page<Product> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        page(page, wrapper);

        // 转换VO
        Page<ProductListVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ProductListVO> list = page.getRecords().stream().map(product -> {
            ProductListVO vo = new ProductListVO();
            BeanUtils.copyProperties(product, vo);
            return vo;
        }).collect(Collectors.toList());
        result.setRecords(list);

        // 3. 存入缓存（5分钟过期）
        redisTemplate.opsForValue().set(cacheKey, result, 5, TimeUnit.MINUTES);

        return result;
    }

    @Override
    public ProductDetailVO getProductDetail(Long productId) {
        String cacheKey = PRODUCT_DETAIL_KEY + productId;

        // 1. 从缓存获取
        ProductDetailVO detail = (ProductDetailVO) redisTemplate.opsForValue().get(cacheKey);
        if (detail != null) {
            log.info("从缓存获取商品详情: {}", productId);
            return detail;
        }

        // 2. 缓存未命中，加锁防止缓存击穿
        RLock lock = redissonClient.getLock("product:lock:" + productId);
        try {
            // 尝试加锁，等待3秒，锁过期时间10秒
            if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                try {
                    // 双重检查
                    detail = (ProductDetailVO) redisTemplate.opsForValue().get(cacheKey);
                    if (detail != null) {
                        return detail;
                    }

                    // 查询数据库
                    Product product = productMapper.getActiveProduct(productId);
                    if (product == null) {
                        return null;
                    }

                    detail = new ProductDetailVO();
                    BeanUtils.copyProperties(product, detail);

                    // 查询SKU列表
                    List<ProductSku> skuList = skuMapper.selectByProductId(productId);
                    List<SkuVO> skuVOList = skuList.stream().map(sku -> {
                        SkuVO skuVO = new SkuVO();
                        BeanUtils.copyProperties(sku, skuVO);
                        return skuVO;
                    }).collect(Collectors.toList());
                    detail.setSkuList(skuVOList);

                    // 存入缓存（10分钟）
                    redisTemplate.opsForValue().set(cacheKey, detail, 10, TimeUnit.MINUTES);

                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            log.error("获取分布式锁异常", e);
        }

        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStock(Long productId, Integer count, List<SkuDeduct> skuList) {
        String stockKey = PRODUCT_STOCK_KEY + productId;

        // 1. 先检查Redis库存是否足够（秒杀场景）
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(DEDUCT_STOCK_LUA, Long.class);
        String skuJson = JSON.toJSONString(skuList);

        Long result = redisTemplate.execute(script,
                Arrays.asList(stockKey, SKU_STOCK_KEY),
                count.toString(), skuJson);

        if (result == null || result == 0) {
            log.warn("Redis库存扣减失败: productId={}", productId);
            return false;
        }

        // 2. 异步扣减数据库库存（发送MQ消息）
        sendDeductStockMessage(productId, count, skuList);

        return true;
    }

    @Override
    public void preheatStock(Long productId) {
        // 查询数据库库存
        Product product = productMapper.selectById(productId);
        if (product != null) {
            String stockKey = PRODUCT_STOCK_KEY + productId;
            redisTemplate.opsForValue().set(stockKey, product.getStock());

            // 预热SKU库存
            List<ProductSku> skuList = skuMapper.selectByProductId(productId);
            skuList.forEach(sku -> {
                String skuKey = SKU_STOCK_KEY + sku.getId();
                redisTemplate.opsForValue().set(skuKey, sku.getStock());
            });

            log.info("商品库存预热完成: productId={}, stock={}", productId, product.getStock());
        }
    }

    /**
     * 发送扣减库存消息（异步）
     */
    private void sendDeductStockMessage(Long productId, Integer count, List<SkuDeduct> skuList) {
        // TODO: 发送到RabbitMQ/Kafka
        // 这里只是示例，实际需要集成MQ
        log.info("发送扣减库存消息: productId={}, count={}", productId, count);
    }

    /**
     * 初始化：加载热门商品库存到Redis
     */
    @PostConstruct
    public void initHotProductStock() {
        // 查询热门商品ID列表
        List<Long> hotProductIds = Arrays.asList(1L, 2L, 3L); // 示例
        hotProductIds.forEach(this::preheatStock);
    }

    @Override
    public ProductSkuVO getSkuInfo(Long skuId) {
        // 1. 先从缓存获取
        String cacheKey = SKU_INFO_KEY + skuId;
        ProductSkuVO cached = (ProductSkuVO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.debug("从缓存获取SKU信息: skuId={}", skuId);
            return cached;
        }

        // 2. 缓存未命中，查询数据库
        ProductSku sku = skuMapper.selectById(skuId);
        if (sku == null) {
            return null;
        }

        // 3. 查询商品信息
        Product product = productMapper.selectById(sku.getProductId());

        // 4. 组装VO
        ProductSkuVO vo = new ProductSkuVO();
        BeanUtils.copyProperties(sku, vo);
        if (product != null) {
            vo.setProductName(product.getName());
        }

        // 5. 存入缓存（30分钟）
        redisTemplate.opsForValue().set(cacheKey, vo, 30, TimeUnit.MINUTES);

        return vo;
    }

    @Override
    public Map<Long, ProductSkuVO> batchGetSkuInfo(List<Long> skuIds) {
        Map<Long, ProductSkuVO> result = new HashMap<>();

        if (skuIds == null || skuIds.isEmpty()) {
            return result;
        }

        // 1. 先从缓存批量获取
        List<String> cacheKeys = skuIds.stream()
                .map(id -> SKU_INFO_KEY + id)
                .collect(Collectors.toList());

        List<Object> cachedList = redisTemplate.opsForValue().multiGet(cacheKeys);

        // 2. 找出未缓存的ID
        List<Long> uncachedIds = new ArrayList<>();
        for (int i = 0; i < skuIds.size(); i++) {
            Long id = skuIds.get(i);
            Object cached = cachedList != null && i < cachedList.size() ? cachedList.get(i) : null;
            if (cached != null) {
                result.put(id, (ProductSkuVO) cached);
            } else {
                uncachedIds.add(id);
            }
        }

        // 3. 查询未缓存的SKU
        if (!uncachedIds.isEmpty()) {
            List<ProductSku> skus = skuMapper.selectBatchIds(uncachedIds);

            // 获取商品信息（批量查询优化）
            Set<Long> productIds = skus.stream()
                    .map(ProductSku::getProductId)
                    .collect(Collectors.toSet());
            Map<Long, Product> productMap = productMapper.selectBatchIds(productIds).stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));

            for (ProductSku sku : skus) {
                ProductSkuVO vo = new ProductSkuVO();
                BeanUtils.copyProperties(sku, vo);

                Product product = productMap.get(sku.getProductId());
                if (product != null) {
                    vo.setProductName(product.getName());
                }

                result.put(sku.getId(), vo);

                // 存入缓存
                redisTemplate.opsForValue().set(SKU_INFO_KEY + sku.getId(), vo, 30, TimeUnit.MINUTES);
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean restoreStock(RestoreStockDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            log.warn("恢复库存失败：商品列表为空");
            return false;
        }

        log.info("恢复库存: orderNo={}, reason={}", dto.getOrderNo(), dto.getReason());

        for (RestoreStockDTO.RestoreItem item : dto.getItems()) {
            // 1. 恢复SKU库存
            int updated = skuMapper.restoreStock(item.getSkuId(), item.getCount());
            if (updated > 0) {
                log.debug("恢复SKU库存: skuId={}, count={}", item.getSkuId(), item.getCount());

                // 2. 清除SKU缓存
                redisTemplate.delete(SKU_INFO_KEY + item.getSkuId());

                // 3. 如果商品有秒杀库存，也恢复Redis中的秒杀库存
                String seckillKey = SECKILL_STOCK_KEY + item.getSkuId();
                redisTemplate.opsForValue().increment(seckillKey, item.getCount());
            }

            // 4. 恢复商品总库存（如果需要）
            // productMapper.restoreTotalStock(item.getProductId(), item.getCount());
        }

        // 5. 清除商品详情缓存（因为库存变了）
        dto.getItems().stream()
                .map(RestoreStockDTO.RestoreItem::getProductId)
                .distinct()
                .forEach(productId -> redisTemplate.delete("product:detail:" + productId));

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStockForOrder(DeductStockForOrderDTO dto) {

        log.info("普通订单扣减库存: orderNo={}", dto.getOrderNo());

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new RuntimeException("商品列表不能为空");
        }

        // 遍历扣减每个SKU的库存
        for (DeductStockForOrderDTO.OrderItem item : dto.getItems()) {
            // 扣减SKU库存（带库存检查）
            int updated = skuMapper.deductStock(item.getSkuId(), item.getCount());

            if (updated == 0) {
                // 扣减失败，回滚事务
                throw new RuntimeException("商品库存不足，SKU ID: " + item.getSkuId());
            }

            log.debug("扣减SKU库存成功: skuId={}, count={}", item.getSkuId(), item.getCount());

            // 清除SKU缓存
            redisTemplate.delete(SKU_INFO_KEY + item.getSkuId());
        }

        return true;
    }


}