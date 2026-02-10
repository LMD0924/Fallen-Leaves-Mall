package org.example.merchantbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * @Author:总会落叶
 * @Date:2026/2/9
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Merchant {
    private Long id;//商家ID
    private Long userId;//用户ID
    private Integer merchantType; //商家类型
    private String merchantName; //商家名称
    private String contactName; //联系人姓名
    private String contactPhone; //联系人电话
    private String contactEmail; //联系人邮箱
    private String businessLicense; //营业执照号
    private String LicenseImage; //营业执照图片
    private String idCard; //身份证号
    private String idCardFront; //身份证正面图片
    private String idCardBack; //身份证背面图片
    private Integer status; //审核状态
    private String rejectReason; //拒绝原因
    private String auditTime; //审核时间
    private String auditor; //审核人
    private String isDeleted; //是否删除
    private String createTime; //创建时间
    private String updateTime; //更新时间
}
