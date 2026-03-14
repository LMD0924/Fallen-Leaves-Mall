package org.example.seckillsession.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SeckillProductVO {
    private Long id;
    private Long sessionId;
    private String sessionName;
    private Long productId;
    private Long skuId;
    private String productName;
    private String productImage;
    private String productDetail;
    private BigDecimal originalPrice;   // 原价
    private BigDecimal seckillPrice;    // 秒杀价
    private Integer seckillStock;       // 秒杀库存
    private Integer seckillLimit;       // 限购
    private Integer soldCount;           // 已售
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // 进度百分比
    public Integer getProgress() {
        if (seckillStock == null || soldCount == null) return 0;
        int total = seckillStock + soldCount;
        if (total == 0) return 0;
        return (int) ((double) soldCount / total * 100);
    }

    // 是否可秒杀
    public Boolean getCanSeckill() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime) && seckillStock > 0;
    }
}