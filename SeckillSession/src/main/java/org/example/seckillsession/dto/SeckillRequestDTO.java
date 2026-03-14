package org.example.seckillsession.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SeckillRequestDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "秒杀商品ID不能为空")
    private Long seckillId;

    private Integer quantity = 1;  // 默认1件
}