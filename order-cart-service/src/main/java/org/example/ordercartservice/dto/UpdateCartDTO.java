package org.example.ordercartservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "更新购物车参数（增强版）")
public class UpdateCartDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", required = true, example = "1001")
    private Long userId;

    @Schema(description = "购物车项ID（单个更新时传）", example = "1")
    private Long cartId;

    @Schema(description = "购物车项ID列表（批量操作时传）")
    private List<Long> cartIds;

    @Min(value = 1, message = "数量必须大于0")
    @Max(value = 999, message = "数量不能超过999")
    @Schema(description = "商品数量（更新数量时传）", example = "2")
    private Integer count;

    @Schema(description = "操作类型", example = "UPDATE_COUNT",
            allowableValues = {"UPDATE_COUNT", "SELECT", "UNSELECT", "DELETE"})
    private String operationType;

    @Schema(description = "选中状态（更新选中状态时传）", example = "true")
    private Boolean selected;

    // 用于校验：要么传cartId，要么传cartIds
    public boolean isValid() {
        if (cartId == null && (cartIds == null || cartIds.isEmpty())) {
            return false;
        }
        return true;
    }

    // 获取操作类型描述
    public String getOperationDesc() {
        if (operationType == null) {
            return "更新数量";
        }
        switch (operationType) {
            case "UPDATE_COUNT":
                return "更新数量";
            case "SELECT":
                return "选中";
            case "UNSELECT":
                return "取消选中";
            case "DELETE":
                return "删除";
            default:
                return "未知操作";
        }
    }
}