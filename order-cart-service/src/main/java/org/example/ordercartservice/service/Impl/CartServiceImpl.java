package org.example.ordercartservice.service.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ordercartservice.client.ProductFeignClient;
import org.example.ordercartservice.dto.AddToCartDTO;
import org.example.ordercartservice.dto.UpdateCartDTO;
import org.example.ordercartservice.entity.Cart;
import org.example.ordercartservice.mapper.CartMapper;
import org.example.ordercartservice.service.CartService;
import org.example.ordercartservice.vo.CartItemVO;
import org.example.ordercartservice.vo.CartVO;
import org.example.ordercartservice.vo.ProductSkuVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    private final CartMapper cartMapper;
    private final ProductFeignClient productFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartVO addToCart(AddToCartDTO dto) {
        // 1. 校验商品库存
        ProductSkuVO sku = productFeignClient.getSkuInfo(dto.getSkuId());
        if (sku == null) {
            throw new RuntimeException("商品不存在");
        }
        if (sku.getStock() < dto.getCount()) {
            throw new RuntimeException("库存不足");
        }

        // 2. 查询是否已存在
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, dto.getUserId())
                .eq(Cart::getSkuId, dto.getSkuId());
        Cart existCart = cartMapper.selectOne(wrapper);

        if (existCart != null) {
            // 已存在，更新数量
            existCart.setCount(existCart.getCount() + dto.getCount());
            cartMapper.updateById(existCart);
        } else {
            // 不存在，新增
            Cart cart = new Cart();
            cart.setUserId(dto.getUserId());
            cart.setProductId(sku.getProductId());
            cart.setSkuId(dto.getSkuId());
            cart.setProductName(sku.getProductName());
            cart.setProductImage(sku.getImage());
            cart.setPrice(sku.getPrice());
            cart.setCount(dto.getCount());
            cart.setSelected(true);
            cartMapper.insert(cart);
        }

        // 3. 返回最新购物车
        return getCartList(dto.getUserId());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartVO updateCart(UpdateCartDTO dto) {
        Cart cart = cartMapper.selectById(dto.getCartId());
        if (cart == null || !cart.getUserId().equals(dto.getUserId())) {
            throw new RuntimeException("购物车商品不存在");
        }

        // 校验库存
        ProductSkuVO sku = productFeignClient.getSkuInfo(cart.getSkuId());
        if (sku.getStock() < dto.getCount()) {
            throw new RuntimeException("库存不足");
        }

        cart.setCount(dto.getCount());
        cartMapper.updateById(cart);

        return getCartList(dto.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFromCart(Long userId, List<Long> cartIds) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .in(Cart::getId, cartIds);
        cartMapper.delete(wrapper);
    }

    @Override
    public CartVO getCartList(Long userId) {
        // 1. 查询购物车列表
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getUpdateTime);
        List<Cart> cartList = cartMapper.selectList(wrapper);

        if (CollectionUtil.isEmpty(cartList)) {
            return CartVO.empty();
        }

        // 2. 校验商品最新价格和库存
        List<Long> skuIds = cartList.stream()
                .map(Cart::getSkuId)
                .collect(Collectors.toList());
        Map<Long, ProductSkuVO> skuMap = productFeignClient.batchGetSkuInfo(skuIds);

        // 3. 组装VO
        List<CartItemVO> items = cartList.stream().map(cart -> {
            CartItemVO item = new CartItemVO();
            item.setId(cart.getId());
            item.setProductId(cart.getProductId());
            item.setSkuId(cart.getSkuId());
            item.setProductName(cart.getProductName());
            item.setProductImage(cart.getProductImage());
            item.setPrice(cart.getPrice());
            item.setCount(cart.getCount());
            item.setSelected(cart.getSelected());

            // 校验最新库存
            ProductSkuVO sku = skuMap.get(cart.getSkuId());
            if (sku != null) {
                item.setStock(sku.getStock());
                item.setValid(sku.getStock() >= cart.getCount() && sku.getStatus() == 1);
                // 如果价格变了，标记出来
                if (sku.getPrice().compareTo(cart.getPrice()) != 0) {
                    item.setPriceChanged(true);
                    item.setCurrentPrice(sku.getPrice());
                }
            }

            return item;
        }).collect(Collectors.toList());

        // 4. 计算总价
        CartVO cartVO = new CartVO();
        cartVO.setItems(items);
        cartVO.calculate();

        return cartVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartVO selectCart(Long userId, List<Long> cartIds, Boolean selected) {
        cartMapper.updateSelected(userId, cartIds, selected);
        return getCartList(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartVO selectAll(Long userId, Boolean selected) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);

        Cart cart = new Cart();
        cart.setSelected(selected);
        cartMapper.update(cart, wrapper);

        return getCartList(userId);
    }
}