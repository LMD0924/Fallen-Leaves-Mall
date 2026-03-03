package org.example.commonbackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.commonbackend.code.LogType;
import org.example.commonbackend.code.OperatorType;

import java.time.LocalDateTime;

/*
 * @Author:总会落叶
 * @Date:2026/3/3
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "mall_operation_log")
public class MallOperationLog {
    private Long id; // 日志唯一主键（自增）
    private String logUuid; // 全局唯一业务追踪ID（UUID）
    private Long tenantId; // 租户ID/商户ID（多租户商城用）
    private LogType logType; // 日志类型
    private OperatorType operatorType; // 操作者类型：1-管理员 2-商家 3-普通用户 4-系统自动
    private String operatorId; // 操作者ID
    private String operatorName; // 操作者姓名/昵称
    private String operatorIp; // 操作者IP地址
    private String businessModule; // 业务模块
    private String businessId; // 业务对象ID
    private String businessNo; // 业务单据号
    private String businessType; // 业务子类型
    private String operationAction; // 操作动作
    private String operationDesc; // 操作描述
    private String oldData; // 操作前数据（JSON）
    private String newData; // 操作后数据（JSON）
    private String changeData; // 变更字段摘要
    private String requestUrl; // 请求URL
    private String requestMethod; // HTTP方法：GET/POST/PUT/DELETE
    private String userAgent; // 用户代理
    private String deviceType; // 设备类型：PC/H5/Android/iOS/小程序
    private String appVersion; // 应用版本号
    private Integer resultStatus; // 执行结果：1-成功 2-失败 3-部分成功
    private String errorCode; // 错误码
    private String errorMsg; // 错误信息
    private Integer executionDuration; // 执行耗时（毫秒）
    private String traceId; // 分布式链路追踪ID
    private Long parentLogId; // 父日志ID
    private LocalDateTime createTime; // 创建时间
}
