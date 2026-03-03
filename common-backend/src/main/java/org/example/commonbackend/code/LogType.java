package org.example.commonbackend.code;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/*
 * @Author:总会落叶
 * @Date:2026/3/2
 * @Description:
 */
@Getter
public enum LogType {
    //日志类型：1-用户行为 2-订单操作 3-商品操作 4-系统操作 5-支付日志 6-营销活动
    USER_BEHAVIOR(1, "用户行为"),
    ORDER_OPERATION(2, "订单操作"),
    PRODUCT_OPERATION(3, "商品操作"),
    SYSTEM_OPERATION(4, "系统操作"),
    PAYMENT_LOG(5, "支付日志"), //支付日志
    MARKETING_ACTIVITY(6, "营销活动"); //营销活动




    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;
    LogType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
