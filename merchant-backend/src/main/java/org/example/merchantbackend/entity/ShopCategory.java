package org.example.merchantbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
 * @Author:总会落叶
 * @Date:2026/3/4
 * @Description: 店铺分类表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("shop_category")
public class ShopCategory {
    @TableId(type = IdType.AUTO)
    private Long id; //分类id
    private Long shopId; //店铺ID
    private String categoryName; //分类名称
    private Long parentId; //父分类ID
    private Integer sort; //排序
    private Integer status; //状态：1->启用；0->禁用
    private Integer isDeleted; //是否删除
    private LocalDateTime createTime; //创建时间
    private LocalDateTime updateTime; //更新时间
}
