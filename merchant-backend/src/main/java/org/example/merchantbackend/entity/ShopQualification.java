package org.example.merchantbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * @Author:总会落叶
 * @Date:2026/3/4
 * @Description: 店铺资质表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("shop_qualification")
public class ShopQualification {
    @TableId(type = IdType.AUTO)
    private Long id; //资质id
    private Long shopId; //店铺ID
    private Integer qualificationType; //资质类型：1->品牌授权；2->质检报告；3->专利证书
    private String title; //资质标题
    private String imageUrl; //资质图片
    private String description; //资质描述
    private LocalDate expireDate; //过期时间
    private Integer status; //状态：1->有效；0->无效
    private LocalDateTime createTime; //创建时间
}
