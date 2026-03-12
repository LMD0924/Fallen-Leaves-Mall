package org.example.merchantbackend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.commonbackend.code.MerchantStatus;

/*
 * @Author:总会落叶
 * @Date:2026/2/9
 * @Description: 商家实体（MyBatis-Plus）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant")
public class Merchant {
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;//商家ID
    private Long userId;//用户ID
    private Integer merchantType; //商家类型
    private String merchantName; //商家名称
    private String contactName; //联系人姓名
    private String contactPhone; //联系人电话
    private String contactEmail; //联系人邮箱
    private String businessLicense; //营业执照号
    @TableField("license_image")
    @JsonProperty("LicenseImage")
    private String licenseImage; //营业执照图片（DB: license_image，JSON 可为 LicenseImage）
    private String idCard; //身份证号
    private String idCardFront; //身份证正面图片
    private String idCardBack; //身份证背面图片
    private MerchantStatus status; //审核状态
    private String rejectReason; //拒绝原因
    private String auditTime; //审核时间
    private String auditor; //审核人
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted; //是否删除（0-未删 1-已删）
    private String createTime; //创建时间
    private String updateTime; //更新时间
}
