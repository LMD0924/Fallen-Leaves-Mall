package org.example.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ProductQueryDTO {

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description="分类ID")
    private Long categoryId;

    @Min(1)
    @Schema(description = "页码")
    private Integer pageNum = 1;

    @Min(1)
    @Schema(description = "每页大小")
    private Integer pageSize = 10;
}