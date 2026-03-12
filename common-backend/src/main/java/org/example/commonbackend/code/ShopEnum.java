package org.example.commonbackend.code;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 店铺相关枚举（包含店铺状态、店铺等级、管理员审核状态）
 * @Author: 总会落叶
 * @Date: 2026/3/11
 */
@Getter
public enum ShopEnum {
    // ========== 店铺运营状态 ==========
    STATUS_NORMAL(1, "正常", "status"),
    STATUS_REST(2, "休息中", "status"),
    STATUS_CLOSED(3, "已关闭", "status"),

    // ========== 店铺等级 ==========
    LEVEL_COMMON(1, "普通", "level"),
    LEVEL_SILVER(2, "银牌", "level"),
    LEVEL_GOLD(3, "金牌", "level"),
    LEVEL_DIAMOND(4, "钻石", "level"),

    // ========== 店铺审核状态 ==========
    AUDIT_PENDING(1, "待审核", "audit"),
    AUDIT_APPROVED(2, "审核通过", "audit"),
    AUDIT_REJECTED(3, "审核不通过", "audit");

    @EnumValue // MyBatis-Plus 注解：指定映射到数据库的字段（code）
    private final Integer code;
    @JsonValue
    private final String desc;
    private final String type;

    ShopEnum(Integer code, String desc, String type) {
        this.code = code;
        this.desc = desc;
        this.type = type;
    }

    // 通用转换方法（保留）
    private static ShopEnum getByCodeAndType(Integer code, String type) {
        if (code == null || type == null || type.isBlank()) {
            return null;
        }
        for (ShopEnum e : ShopEnum.values()) {
            if (type.equals(e.getType()) && code.equals(e.getCode())) {
                return e;
            }
        }
        return null;
    }

    // 各类型专用方法（保留）
    public static ShopEnum getStatusByCode(Integer code) {
        return getByCodeAndType(code, "status");
    }

    public static ShopEnum getLevelByCode(Integer code) {
        return getByCodeAndType(code, "level");
    }

    public static ShopEnum getAuditStatusByCode(Integer code) {
        return getByCodeAndType(code, "audit");
    }

    // 获取描述方法（保留）
    public static String getStatusDesc(Integer code) {
        ShopEnum status = getStatusByCode(code);
        return status != null ? status.getDesc() : "未知店铺状态";
    }

    public static String getLevelDesc(Integer code) {
        ShopEnum level = getLevelByCode(code);
        return level != null ? level.getDesc() : "未知店铺等级";
    }

    public static String getAuditStatusDesc(Integer code) {
        ShopEnum auditStatus = getAuditStatusByCode(code);
        return auditStatus != null ? auditStatus.getDesc() : "未知审核状态";
    }
}