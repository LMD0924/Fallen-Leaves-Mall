package org.example.couponservice.service.Impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.couponservice.dto.ReceiveCouponDTO;
import org.example.couponservice.dto.UseCouponDTO;
import org.example.couponservice.entity.Coupon;
import org.example.couponservice.entity.CouponReceiveLog;
import org.example.couponservice.entity.CouponScope;
import org.example.couponservice.entity.UserCoupon;
import org.example.couponservice.mapper.CouponMapper;
import org.example.couponservice.mapper.CouponReceiveLogMapper;
import org.example.couponservice.mapper.CouponScopeMapper;
import org.example.couponservice.mapper.UserCouponMapper;
import org.example.couponservice.service.CouponService;
import org.example.couponservice.vo.CouponVO;
import org.example.couponservice.vo.UserCouponVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;
    private final CouponScopeMapper couponScopeMapper;
    private final CouponReceiveLogMapper receiveLogMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;
    private final HttpServletRequest request;

    // Redis key前缀
    private static final String COUPON_STOCK_KEY = "coupon:stock:";
    private static final String COUPON_USER_KEY = "coupon:user:";
    private static final String COUPON_RECEIVE_LOCK = "coupon:receive:lock:";

    // Lua脚本：原子扣减库存
    private static final String DEDUCT_STOCK_LUA =
            "local stock_key = KEYS[1] " +
                    "local stock = redis.call('get', stock_key) " +
                    "if not stock or tonumber(stock) <= 0 then " +
                    "    return 0 " +
                    "end " +
                    "redis.call('decr', stock_key) " +
                    "return 1";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserCouponVO receiveCoupon(ReceiveCouponDTO dto) {
        Long couponId = dto.getCouponId();
        Long userId = dto.getUserId();

        // 1. 分布式锁（防止同一用户并发领取）
        String lockKey = COUPON_RECEIVE_LOCK + userId + ":" + couponId;
        //获取锁对象
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试获取锁，最多等待3秒，上锁以后10秒自动解锁
            // 注意：这里的阻塞式等待，如果用户在等待期间获取到锁，那么就会导致其他用户无法获取锁
            // 解决方案：使用非阻塞式等待，如果获取不到锁，则直接返回失败
            // 这里使用的是阻塞式等待，因为如果用户在等待期间获取到锁，那么就会导致其他用户无法获取锁
            // 解决方案：使用非阻塞式等待，如果获取不到锁，则直接返回失败
            // 这里使用的是阻塞式等待，
            if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                log.warn("获取锁失败，用户: {}, 优惠券: {}", userId, couponId);
                throw new RuntimeException("操作太频繁，请稍后重试");
            }

            // 2. 校验是否可领取（Redis缓存判断）
            String stockKey = COUPON_STOCK_KEY + couponId;
            Integer stock = (Integer) redisTemplate.opsForValue().get(stockKey);

            if (stock == null) {
                // Redis无缓存，从数据库加载
                Coupon coupon = couponMapper.selectById(couponId);
                if (coupon == null) {
                    throw new RuntimeException("优惠券不存在");
                }
                stock = coupon.getStock();
                redisTemplate.opsForValue().set(stockKey, stock, 1, TimeUnit.HOURS);
            }

            if (stock <= 0) {
                throw new RuntimeException("优惠券已领完");
            }

            // 3. 校验每人限领
            String userKey = COUPON_USER_KEY + userId + ":" + couponId;
            Integer userCount = (Integer) redisTemplate.opsForValue().get(userKey);

            if (userCount == null) {
                userCount = userCouponMapper.countUserReceived(userId, couponId);
                redisTemplate.opsForValue().set(userKey, userCount, 1, TimeUnit.HOURS);
            }

            // 查询优惠券信息获取限领数量
            Coupon coupon = couponMapper.selectById(couponId);
            if (userCount >= coupon.getPerUserLimit()) {
                throw new RuntimeException("已达到领取上限");
            }

            // 4. Redis原子扣减库存
            DefaultRedisScript<Long> script = new DefaultRedisScript<>(DEDUCT_STOCK_LUA, Long.class);
            Long result = redisTemplate.execute(script, Arrays.asList(stockKey));

            if (result == null || result == 0) {
                throw new RuntimeException("优惠券已领完");
            }

            // 5. 更新用户领取计数
            redisTemplate.opsForValue().increment(userKey);

            // 6. 生成唯一券码
            String code = generateCouponCode(couponId, userId);

            // 7. 保存用户优惠券
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUserId(userId);
            userCoupon.setCouponId(couponId);
            userCoupon.setCode(code);
            userCoupon.setFaceValue(coupon.getFaceValue());
            userCoupon.setCondition(coupon.getCondition());
            userCoupon.setType(coupon.getType());
            userCoupon.setStatus(0); // 未使用
            userCoupon.setStartTime(coupon.getUseStartTime());
            userCoupon.setEndTime(coupon.getUseEndTime());
            userCoupon.setCreateTime(LocalDateTime.now());
            userCoupon.setUpdateTime(LocalDateTime.now());
            userCouponMapper.insert(userCoupon);

            // 8. 异步更新数据库库存（通过MQ）
            sendUpdateStockMessage(couponId);

            // 9. 记录日志
            CouponReceiveLog log = new CouponReceiveLog();
            log.setCouponId(couponId);
            log.setUserId(userId);
            log.setStatus(1);
            log.setIp(getClientIp());
            receiveLogMapper.insert(log);

            // 10. 返回VO
            UserCouponVO vo = new UserCouponVO();
            BeanUtils.copyProperties(userCoupon, vo);
            return vo;

        } catch (InterruptedException e) {
            log.error("领取优惠券异常", e);
            throw new RuntimeException("系统繁忙，请稍后重试");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public Page<CouponVO> getAvailableCoupons(Integer pageNum, Integer pageSize) {
        // 优先从缓存获取
        String cacheKey = "coupon:available:page:" + pageNum + ":" + pageSize;
        Page<CouponVO> cachePage = (Page<CouponVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachePage != null) {
            return cachePage;
        }

        // 查询数据库
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getStatus, 1)
                .le(Coupon::getReceiveStartTime, LocalDateTime.now())
                .ge(Coupon::getReceiveEndTime, LocalDateTime.now())
                .gt(Coupon::getStock, 0)
                .orderByDesc(Coupon::getCreateTime);

        Page<Coupon> page = new Page<>(pageNum, pageSize);
        page(page, wrapper);

        // 转换VO
        Page<CouponVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<CouponVO> list = page.getRecords().stream().map(coupon -> {
            CouponVO vo = new CouponVO();
            BeanUtils.copyProperties(coupon, vo);

            // 从Redis获取实时库存
            String stockKey = COUPON_STOCK_KEY + coupon.getId();
            Integer stock = (Integer) redisTemplate.opsForValue().get(stockKey);
            vo.setStock(stock != null ? stock : coupon.getStock());

            return vo;
        }).collect(Collectors.toList());
        result.setRecords(list);

        // 缓存5分钟
        redisTemplate.opsForValue().set(cacheKey, result, 5, TimeUnit.MINUTES);

        return result;
    }

    @Override
    public Page<UserCouponVO> getUserCoupons(Long userId, Integer status,
                                             Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        if (status != null) {
            wrapper.eq(UserCoupon::getStatus, status);
        }
        wrapper.orderByDesc(UserCoupon::getCreateTime);

        Page<UserCoupon> page = new Page<>(pageNum, pageSize);
        userCouponMapper.selectPage(page, wrapper);

        Page<UserCouponVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<UserCouponVO> list = page.getRecords().stream().map(uc -> {
            UserCouponVO vo = new UserCouponVO();
            BeanUtils.copyProperties(uc, vo);

            // 查询优惠券名称
            Coupon coupon = couponMapper.selectById(uc.getCouponId());
            if (coupon != null) {
                vo.setCouponName(coupon.getName());
            }

            return vo;
        }).collect(Collectors.toList());
        result.setRecords(list);

        return result;
    }

    @Override
    public List<UserCouponVO> getApplicableCoupons(Long userId, BigDecimal totalAmount,
                                                   List<Long> productIds, List<Long> categoryIds) {
        // 查询用户可用优惠券
        List<UserCoupon> coupons = userCouponMapper.selectUserAvailableCoupons(userId);

        // 过滤出符合条件的
        List<UserCouponVO> result = coupons.stream()
                .filter(uc -> isCouponApplicable(uc, totalAmount, productIds, categoryIds))
                .map(uc -> {
                    UserCouponVO vo = new UserCouponVO();
                    BeanUtils.copyProperties(uc, vo);

                    // 计算优惠金额
                    vo.setDiscountAmount(calculateDiscount(uc, totalAmount));

                    return vo;
                })
                .sorted((a, b) -> b.getDiscountAmount().compareTo(a.getDiscountAmount()))
                .collect(Collectors.toList());

        return result;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean useCoupon(UseCouponDTO dto) {
        // 1. 校验优惠券
        UserCoupon userCoupon = userCouponMapper.selectById(dto.getUserCouponId());
        if (userCoupon == null || !userCoupon.getUserId().equals(dto.getUserId())) {
            throw new RuntimeException("优惠券不存在");
        }

        if (userCoupon.getStatus() != 0) {
            throw new RuntimeException("优惠券不可用");
        }

        if (userCoupon.getEndTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("优惠券已过期");
        }

        // 2. 校验使用条件
        if (userCoupon.getCondition() != null &&
                userCoupon.getCondition().compareTo(dto.getOrderAmount()) > 0) {
            throw new RuntimeException("未达到使用条件");
        }

        //TODO:校验订单是否存在

        // 3. 更新状态
        int updated = userCouponMapper.useCoupon(
                dto.getUserCouponId(),
                dto.getOrderId(),
                dto.getOrderNo()
        );

        return updated > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refundCoupon(Long userCouponId, Long orderId) {
        //TODO：无论ID为多少都能操作成功，当信息正确时，却没有退还，只是显示退还成功

        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || !orderId.equals(userCoupon.getOrderId())) {
            return false;
        }

        // 检查是否在有效期内（如果已过期则不能退还）
        if (userCoupon.getEndTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        userCoupon.setStatus(0);
        userCoupon.setUseTime(null);
        userCoupon.setOrderId(null);
        userCoupon.setOrderNo(null);
        userCouponMapper.updateById(userCoupon);

        return true;
    }

    @Override
    public void preheatCouponStock(Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon != null) {
            String stockKey = COUPON_STOCK_KEY + couponId;
            redisTemplate.opsForValue().set(stockKey, coupon.getStock());
            log.info("优惠券库存预热完成: couponId={}, stock={}", couponId, coupon.getStock());
        }
    }

    /**
     * 定时任务：过期优惠券
     */
    @Scheduled(cron = "0 0/5 * * * ?") // 每5分钟执行一次
    public void expireCoupons() {
        log.info("开始执行优惠券过期任务");
        int count = userCouponMapper.batchExpireCoupons();
        log.info("优惠券过期任务完成，过期数量：{}", count);
    }

    /**
     * 定时任务：同步Redis库存到数据库
     */
    @Scheduled(cron = "0 0/30 * * * ?") // 每30分钟执行一次
    public void syncStockToDB() {
        log.info("开始同步优惠券库存");

        // 获取所有优惠券ID
        Set<String> keys = redisTemplate.keys(COUPON_STOCK_KEY + "*");
        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            String couponIdStr = key.replace(COUPON_STOCK_KEY, "");
            Long couponId = Long.parseLong(couponIdStr);
            Integer stock = (Integer) redisTemplate.opsForValue().get(key);

            if (stock != null) {
                Coupon coupon = new Coupon();
                coupon.setId(couponId);
                coupon.setStock(stock);
                couponMapper.updateById(coupon);
            }
        }

        log.info("库存同步完成");
    }

    /**
     * 初始化：预热热门优惠券库存
     */
    @PostConstruct
    public void initHotCoupons() {
        List<Coupon> hotCoupons = couponMapper.selectList(
                new LambdaQueryWrapper<Coupon>()
                        .eq(Coupon::getStatus, 1)
                        .gt(Coupon::getStock, 0)
                        .last("LIMIT 10")
        );

        hotCoupons.forEach(coupon -> preheatCouponStock(coupon.getId()));
    }

    /**
     * 生成唯一券码
     * 格式：C + 日期(8位) + 随机数(6位) + 校验码
     */
    private String generateCouponCode(Long couponId, Long userId) {
        String dateStr = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = IdUtil.fastSimpleUUID().substring(0, 8).toUpperCase();
        String checkCode = Integer.toHexString((int) (couponId + userId + System.currentTimeMillis())).substring(0, 2);
        return "C" + dateStr + randomStr + checkCode;
    }

    /**
     * 判断优惠券是否适用于当前订单
     */
    private boolean isCouponApplicable(UserCoupon userCoupon, BigDecimal totalAmount,
                                       List<Long> productIds, List<Long> categoryIds) {
        Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());

        // 全场通用
        if (coupon.getScopeType() == 1) {
            return true;
        }

        // 指定商品/分类
        List<CouponScope> scopes = couponScopeMapper.selectByCouponId(coupon.getId());

        if (coupon.getScopeType() == 2) { // 指定商品
            for (Long productId : productIds) {
                if (scopes.stream().anyMatch(s -> s.getScopeId().equals(productId))) {
                    return true;
                }
            }
        } else if (coupon.getScopeType() == 3) { // 指定分类
            for (Long categoryId : categoryIds) {
                if (scopes.stream().anyMatch(s -> s.getScopeId().equals(categoryId))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 计算优惠金额
     */
    private BigDecimal calculateDiscount(UserCoupon userCoupon, BigDecimal totalAmount) {
        if (userCoupon.getType() == 1) { // 满减券
            return userCoupon.getFaceValue();
        } else if (userCoupon.getType() == 2) { // 折扣券
            return totalAmount.multiply(BigDecimal.ONE.subtract(
                    userCoupon.getFaceValue().divide(new BigDecimal(10))));
        } else if (userCoupon.getType() == 3) { // 无门槛
            return userCoupon.getFaceValue();
        }
        return BigDecimal.ZERO;
    }

    /**
     * 发送更新库存消息
     */
    private void sendUpdateStockMessage(Long couponId) {
        // TODO: 发送到MQ
        log.info("发送优惠券库存更新消息: couponId={}", couponId);
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}