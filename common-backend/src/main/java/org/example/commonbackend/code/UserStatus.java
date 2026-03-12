package org.example.commonbackend.code;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/*
 * @Author:总会落叶
 * @Date:2026/2/28
 * @Description:
 */
@Getter
public enum UserStatus {
    NORMAL(0, "正常"),
    FORBIDDEN(1, "禁用"),

    //vip等级
    VIP1(1,"普通"),
    VIP2(2,"VIP"),
    VIP3(3,"VIP2");


    @EnumValue
    private final int value;
    @JsonValue
    private final String desc;

    UserStatus(int value,String desc){
        this.value = value;
        this.desc = desc;
    }

    // 根据代码获取枚举值
    public static UserStatus getByCode(int code) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getValue() == code) {
                return status;
            }
        }
        return NORMAL; // 默认返回正常状态
    }
}
