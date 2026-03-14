package org.example.ordercartservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "扣减库存参数")
public class DeductStockDTO {

    @Schema(description = "订单号", example = "20240301123456789")
    private String orderNo;

    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", required = true, example = "1")
    private Long productId;

    @NotNull(message = "扣减数量不能为空")
    @Min(value = 1, message = "扣减数量必须大于0")
    @Schema(description = "扣减数量", required = true, example = "1")
    private Integer count;

    @Schema(description = "操作类型：1下单扣减 2取消订单回滚", example = "1")
    private Integer operationType;

    @Schema(description = "是否秒杀商品", example = "false")
    private Boolean isSeckill;
}