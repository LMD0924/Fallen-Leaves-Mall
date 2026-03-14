package org.example.ordercartservice.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartVO {
    private List<CartItemVO> items;
    private Integer totalCount;      // 商品总数量
    private Integer selectedCount;   // 选中商品数量
    private BigDecimal totalPrice;   // 商品总价
    private BigDecimal selectedPrice;// 选中商品总价
    private Boolean allSelected;     // 是否全选

    public void calculate() {
        totalCount = items.stream()
                .mapToInt(CartItemVO::getCount)
                .sum();

        selectedCount = items.stream()
                .filter(CartItemVO::getSelected)
                .mapToInt(CartItemVO::getCount)
                .sum();

        totalPrice = items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        selectedPrice = items.stream()
                .filter(CartItemVO::getSelected)
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        allSelected = items.stream().allMatch(CartItemVO::getSelected);
    }

    public static CartVO empty() {
        CartVO vo = new CartVO();
        vo.setTotalCount(0);
        vo.setSelectedCount(0);
        vo.setTotalPrice(BigDecimal.ZERO);
        vo.setSelectedPrice(BigDecimal.ZERO);
        vo.setAllSelected(false);
        return vo;
    }
}