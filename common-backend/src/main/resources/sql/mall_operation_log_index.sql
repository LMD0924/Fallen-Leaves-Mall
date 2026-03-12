-- MallOperationLog表索引优化

-- 主键索引（默认已存在）
-- ALTER TABLE mall_operation_log ADD PRIMARY KEY (id);

-- 日志UUID索引（用于详情查询）
CREATE INDEX idx_mall_operation_log_log_uuid ON mall_operation_log(log_uuid);

-- 时间索引（用于时间范围查询）
CREATE INDEX idx_mall_operation_log_create_time ON mall_operation_log(create_time);

-- 日志类型索引（用于按日志类型查询）
CREATE INDEX idx_mall_operation_log_log_type ON mall_operation_log(log_type);

-- 业务模块索引（用于按业务模块查询）
CREATE INDEX idx_mall_operation_log_business_module ON mall_operation_log(business_module);

-- 操作者索引（用于按操作者查询）
CREATE INDEX idx_mall_operation_log_operator ON mall_operation_log(operator_name, operator_id);

-- 业务ID/单号索引（用于按业务ID/单号查询）
CREATE INDEX idx_mall_operation_log_business ON mall_operation_log(business_id, business_no);

-- 结果状态索引（用于按执行结果查询）
CREATE INDEX idx_mall_operation_log_result_status ON mall_operation_log(result_status);

-- 组合索引（用于常见查询场景）
CREATE INDEX idx_mall_operation_log_type_time ON mall_operation_log(log_type, create_time);
CREATE INDEX idx_mall_operation_log_module_time ON mall_operation_log(business_module, create_time);
