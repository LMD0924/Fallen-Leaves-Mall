package org.example.merchantbackend.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.commonbackend.code.MerchantStatus;

/*
 * @Author:总会落叶
 * @Date:2026/3/11
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditMerchantDTO {
    private Long id;
    private Long adminId;
    private Long userId;
    private MerchantStatus status;
    private String rejectReason;
}
