package org.example.commonbackend.code;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/*
 * @Author:总会落叶
 * @Date:2026/2/28
 * @Description:
 */
@Getter
public enum UserStatus {
    NORMAL(0, "正常"),
    FORBIDDEN(1, "禁用");

    @EnumValue
    private final int value;
    private final String desc;

    UserStatus(int value,String desc){
        this.value = value;
        this.desc = desc;
    }
}
