package org.example.commonbackend.code;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/*
 * @Author:总会落叶
 * @Date:2026/2/28
 * @Description:
 */
@Getter
public enum MerchantStatus {
    PENDING_REVIEW(0, "待审核"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已拒绝"),
    DISABLE(3,"已禁用");

    @EnumValue
    private final int code;
    private final String desc;

    MerchantStatus(int code,String desc){
        this.code = code;
        this.desc = desc;
    }
}
