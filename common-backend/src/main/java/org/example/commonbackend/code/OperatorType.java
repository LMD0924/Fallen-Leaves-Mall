package org.example.commonbackend.code;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

/*
 * @Author:总会落叶
 * @Date:2026/3/3
 * @Description:
 */
@Getter
public enum OperatorType {
    //操作者类型：1-管理员 2-商家 3-普通用户 4-系统自动
    ADMIN(1, "管理员"),
    MERCHANT(2, "商家"),
    USER(3, "普通用户"),
    SYSTEM(4, "系统自动");
    @EnumValue // 关键注解：告诉 MyBatis-Plus 存数据库时用这个值
    private final Integer code;
    @JsonValue
    private final String desc;
    OperatorType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OperatorType fromCode(Integer code) {
        for (OperatorType type : OperatorType.values()) {
            if (Objects.equals(type.code, code)) {
                return type;
            }
        }
        return null;
    }
}
