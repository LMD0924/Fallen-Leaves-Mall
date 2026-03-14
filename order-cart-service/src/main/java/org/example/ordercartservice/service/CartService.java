package org.example.ordercartservice.service;



import org.example.ordercartservice.dto.AddToCartDTO;
import org.example.ordercartservice.dto.UpdateCartDTO;
import org.example.ordercartservice.vo.CartVO;

import java.util.List;

public interface CartService {

    /**
     * 添加商品到购物车
     */
    CartVO addToCart(AddToCartDTO dto);

    /**
     * 更新购物车商品数量
     */
    CartVO updateCart(UpdateCartDTO dto);

    /**
     * 删除购物车商品
     */
    void removeFromCart(Long userId, List<Long> cartIds);

    /**
     * 获取购物车列表
     */
    CartVO getCartList(Long userId);

    /**
     * 选中/取消选中
     */
    CartVO selectCart(Long userId, List<Long> cartIds, Boolean selected);

    /**
     * 全选/全不选
     */
    CartVO selectAll(Long userId, Boolean selected);
}