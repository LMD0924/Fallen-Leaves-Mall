package org.example.merchantbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.commonbackend.code.ShopEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private String shopIntro; //店铺简介
    private String shopNotice; //店铺公告
    private String contactQq; //联系qq
    private String contactWechat; //联系微信
    private ShopEnum shopLevel; //店铺等级：1->普通；2->银牌；3->金牌；4->钻石
    private BigDecimal shopScore; //店铺评分
    private Integer monthSales; //月销量
    private Integer totalSales; //总销量
    private Integer productCount; //商品数量
    private Integer followerCount; //关注人数
    private BigDecimal serviceScore; //服务评分
    private BigDecimal deliveryScore; //物流评分
    private BigDecimal descriptionScore; //描述评分
    private ShopEnum status; //状态：1->正常；2->休息中；3->已关闭
    private Integer isRecommend; //是否推荐店铺
    private Integer isVerified; //是否认证店铺
    private Integer isDeleted; //是否删除
    private ShopEnum shopStatus; //店铺审核状态 ：1->待审核；2->审核通过；3->审核不通过
    private String rejectReason; //审核不通过原因
}
