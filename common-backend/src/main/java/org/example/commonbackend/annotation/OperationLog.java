package org.example.commonbackend.annotation;

import org.example.commonbackend.code.LogType;
import org.example.commonbackend.code.OperatorType;

import java.lang.annotation.*;

/*
 * @Author:总会落叶
 * @Date:2026/3/2
 * @Description: 日志注解
 */
@Target({ElementType.METHOD})//可以标注在方法上
@Retention(RetentionPolicy.RUNTIME)//运行时有效
@Documented//生成文档
public @interface OperationLog {


    /**
     * 日志类型：1-用户行为 2-订单操作 3-商品操作 4-系统操作 5-支付日志 6-营销活动
     */
    LogType logType() default LogType.USER_BEHAVIOR;

    /**
    * 操作者类型：1-管理员 2-商家 3-普通用户 4-系统自动
    */
    OperatorType operatorType() default OperatorType.SYSTEM;

    /**
     * 业务模块：order/product/user/marketing/payment
     */
    String businessModule() default "";

    /**
     * 业务子类型：如订单-创建/支付/取消
     */
    String businessType() default "";

    /**
     * 操作动作：INSERT/UPDATE/DELETE/LOGIN/EXPORT/UPLOAD
     */
    String operationAction() default "";

    /**
     * 操作描述模板（支持SpEL表达式）
     * 例如：用户 ${#userName} 修改了订单 ${#orderNo}
     */
    String description() default "";

    /**
     * 是否记录请求参数
     */
    boolean recordParams() default true;

    /**
     * 是否记录返回结果
     */
    boolean recordResult() default true;

    /**
     * 是否记录旧数据（用于更新操作）
     */
    boolean recordOldData() default false;

    /**
     * 业务ID的SpEL表达式（从参数中获取）
     */
    String businessIdSpel() default "";

    /**
     * 业务单号的SpEL表达式（从参数中获取）
     */
    String businessNoSpel() default "";

    /**
     * 是否保存方法执行耗时
     */
    boolean saveDuration() default true;
}
