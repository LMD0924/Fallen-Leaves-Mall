package org.example.productservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String subtitle;
    private Long categoryId;
    private Long brandId;
    private BigDecimal price;
    private Integer stock;
    private Integer soldCount;
    private Integer status;
    private String mainImage;
    private String detail;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime = LocalDateTime.now();  // 设置默认值

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime = LocalDateTime.now();  // 设置默认值

}