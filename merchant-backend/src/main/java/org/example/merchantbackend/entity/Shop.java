package org.example.merchantbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * @Author:总会落叶
 * @Date:2026/2/20
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("shop")
public class Shop {
   @TableId(type = IdType.AUTO)
    private Long id; //店铺id
   private Long merchantId; //商家id
    private String shopName; //店铺名称
    private String shopLogo; //店铺logo
    private String shopBanner; //店铺banner
    private String shopInto; //店铺简介
    private String shopNotice; //店铺公告
    private String contactQq; //联系qq
    private String contactWechat; //联系微信
    private String shopLevel; //店铺等级
    private Double shopScore; //店铺评分
    private Integer monthSales; //月销量
    private Long totalSales; //总销量
    private Long productCount; //商品数量
    private Long followerCount; //关注数量
    private Double serviceScore; //服务评分
    private Double deliveryScore; //配送评分
    private Double descriptionScore; //描述评分
    private Integer status; //店铺状态
    private Integer isRecommend; //是否推荐
    private Integer isVerified; //是否认证
    private Integer isDeleted; //是否删除
    private Long createTime; //创建时间
    private Long updateTime; //更新时间
    private String shopStatus; //店铺审核状态
}
