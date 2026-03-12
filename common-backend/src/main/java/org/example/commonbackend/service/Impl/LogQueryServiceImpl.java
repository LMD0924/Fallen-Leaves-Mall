package org.example.commonbackend.service.Impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.example.commonbackend.code.LogType;
import org.example.commonbackend.controller.LogController;
import org.example.commonbackend.entity.MallOperationLog;
import org.example.commonbackend.mapper.LogMapper;
import org.example.commonbackend.redis.LogRedisKey;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogQueryServiceImpl {

    private final RedisTemplate<String, String> redisTemplate;
    private final LogMapper operatorLogMapper;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LogController.LogPageResponse queryLogs(LogController.LogQueryRequest request) {
        try {
            // 尝试从Redis获取
            List<MallOperationLog> logs = queryLogsFromRedis(request);
            if (logs != null && !logs.isEmpty()) {
                long total = getTotalFromRedis(request);
                return buildPageResponse(logs, total, request.getPage(), request.getPageSize());
            }
            // Redis未命中，从数据库查询
            return queryLogsFromDatabase(request);
        } catch (Exception e) {
            log.error("查询日志失败", e);
            // 异常时从数据库查询
            return queryLogsFromDatabase(request);
        }
    }

    public MallOperationLog getLogByUuid(String logUuid) {
        try {
            // 尝试从Redis获取
            String key = String.format(LogRedisKey.LOG_DETAIL, logUuid);
            String logJson = redisTemplate.opsForValue().get(key);
            if (logJson != null) {
                return JSON.parseObject(logJson, MallOperationLog.class);
            }
            // Redis未命中，从数据库查询
            QueryWrapper<MallOperationLog> wrapper = new QueryWrapper<>();
            wrapper.eq("log_uuid", logUuid);
            return operatorLogMapper.selectOne(wrapper);
        } catch (Exception e) {
            log.error("获取日志详情失败", e);
            // 异常时从数据库查询
            QueryWrapper<MallOperationLog> wrapper = new QueryWrapper<>();
            wrapper.eq("log_uuid", logUuid);
            return operatorLogMapper.selectOne(wrapper);
        }
    }

    public LogController.LogStatResponse getLogStatistics() {
        LogController.LogStatResponse stats = new LogController.LogStatResponse();
        try {
            // 从数据库查询统计数据
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();

            // 今日日志数
            QueryWrapper<MallOperationLog> todayWrapper = new QueryWrapper<>();
            todayWrapper.ge("create_time", startOfDay);
            stats.setTodayCount(operatorLogMapper.selectCount(todayWrapper));

            // 用户操作数
            QueryWrapper<MallOperationLog> userWrapper = new QueryWrapper<>();
            userWrapper.eq("log_type", LogType.USER_BEHAVIOR.getCode());
            stats.setUserOperCount(operatorLogMapper.selectCount(userWrapper));

            // 订单操作数
            QueryWrapper<MallOperationLog> orderWrapper = new QueryWrapper<>();
            orderWrapper.eq("log_type", LogType.ORDER_OPERATION.getCode());
            stats.setOrderOperCount(operatorLogMapper.selectCount(orderWrapper));

            // 失败操作数
            QueryWrapper<MallOperationLog> failWrapper = new QueryWrapper<>();
            failWrapper.eq("result_status", 2);
            stats.setFailCount(operatorLogMapper.selectCount(failWrapper));
        } catch (Exception e) {
            log.error("获取日志统计失败", e);
        }
        return stats;
    }

    public ByteArrayOutputStream exportLogs(LogController.LogQueryRequest request) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("操作日志");

        try {
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"时间", "操作者", "操作类型", "业务模块", "操作描述", "业务ID", "业务单号", "结果", "IP地址", "耗时(ms)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle style = workbook.createCellStyle();
                style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                style.setBorderTop(BorderStyle.THIN);
                style.setBorderBottom(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
                style.setBorderRight(BorderStyle.THIN);
                cell.setCellStyle(style);
            }

            // 查询数据
            request.setPage(1);
            request.setPageSize(1000); // 限制导出数量
            LogController.LogPageResponse response = queryLogs(request);
            List<MallOperationLog> logs = response.getList();

            // 填充数据
            for (int i = 0; i < logs.size(); i++) {
                MallOperationLog log = logs.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(log.getCreateTime() != null ? log.getCreateTime().format(DATETIME_FORMAT) : "");
                row.createCell(1).setCellValue(log.getOperatorName() != null ? log.getOperatorName() : "");
                row.createCell(2).setCellValue(log.getLogType() != null ? log.getLogType().getDesc() : "");
                row.createCell(3).setCellValue(log.getBusinessModule() != null ? log.getBusinessModule() : "");
                row.createCell(4).setCellValue(log.getOperationDesc() != null ? log.getOperationDesc() : "");
                row.createCell(5).setCellValue(log.getBusinessId() != null ? log.getBusinessId() : "");
                row.createCell(6).setCellValue(log.getBusinessNo() != null ? log.getBusinessNo() : "");
                row.createCell(7).setCellValue(log.getResultStatus() == 1 ? "成功" : (log.getResultStatus() == 2 ? "失败" : "部分成功"));
                row.createCell(8).setCellValue(log.getOperatorIp() != null ? log.getOperatorIp() : "");
                row.createCell(9).setCellValue(log.getExecutionDuration() != null ? log.getExecutionDuration() : 0);
            }

            // 调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        } catch (Exception e) {
            log.error("导出日志失败", e);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                log.error("关闭工作簿失败", e);
            }
        }
        return outputStream;
    }

    public boolean batchDeleteLogs(List<String> logUuids) {
        try {
            for (String logUuid : logUuids) {
                deleteLog(logUuid);
            }
            return true;
        } catch (Exception e) {
            log.error("批量删除日志失败", e);
            return false;
        }
    }

    public boolean deleteLog(String logUuid) {
        try {
            // 删除Redis中的数据
            String detailKey = String.format(LogRedisKey.LOG_DETAIL, logUuid);
            redisTemplate.delete(detailKey);

            // 删除数据库中的数据
            QueryWrapper<MallOperationLog> wrapper = new QueryWrapper<>();
            wrapper.eq("log_uuid", logUuid);
            operatorLogMapper.delete(wrapper);

            return true;
        } catch (Exception e) {
            log.error("删除日志失败", e);
            return false;
        }
    }

    private List<MallOperationLog> queryLogsFromRedis(LogController.LogQueryRequest request) {
        try {
            // 对于简单查询，尝试从Redis获取
            if (isSimpleQuery(request)) {
                List<MallOperationLog> logs = new ArrayList<>();

                // 根据日志类型和日期查询
                if (request.getLogType() != null && request.getStartTime() != null && request.getEndTime() != null) {
                    LocalDate startDate = request.getStartTime().toLocalDate();
                    LocalDate endDate = request.getEndTime().toLocalDate();

                    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                        String dateStr = date.format(DATE_FORMAT);
                        String dateKey = String.format(LogRedisKey.LOG_DATE_INDEX, dateStr, request.getLogType());
                        List<String> logUuids = redisTemplate.opsForList().range(dateKey, 0, -1);

                        if (logUuids != null) {
                            for (String logUuid : logUuids) {
                                String detailKey = String.format(LogRedisKey.LOG_DETAIL, logUuid);
                                String logJson = redisTemplate.opsForValue().get(detailKey);
                                if (logJson != null) {
                                    MallOperationLog log = JSON.parseObject(logJson, MallOperationLog.class);
                                    // 检查时间范围
                                    if (log.getCreateTime() != null &&
                                            !log.getCreateTime().isBefore(request.getStartTime()) &&
                                            !log.getCreateTime().isAfter(request.getEndTime())) {
                                        logs.add(log);
                                    }
                                }
                            }
                        }
                    }
                }

                // 按业务模块查询
                else if (request.getBusinessModule() != null && request.getBusinessNo() != null) {
                    String bizKey = String.format(LogRedisKey.LOG_BIZ_LIST, request.getBusinessModule(), request.getBusinessNo());
                    List<String> logUuids = redisTemplate.opsForList().range(bizKey, 0, -1);

                    if (logUuids != null) {
                        for (String logUuid : logUuids) {
                            String detailKey = String.format(LogRedisKey.LOG_DETAIL, logUuid);
                            String logJson = redisTemplate.opsForValue().get(detailKey);
                            if (logJson != null) {
                                logs.add(JSON.parseObject(logJson, MallOperationLog.class));
                            }
                        }
                    }
                }

                // 按操作者查询
                else if (request.getOperator() != null) {
                    // 尝试从不同操作类型的索引中查询
                    for (int operatorType = 1; operatorType <= 4; operatorType++) {
                        String operatorKey = String.format(LogRedisKey.LOG_OPERATOR_LIST, operatorType, request.getOperator());
                        List<String> logUuids = redisTemplate.opsForList().range(operatorKey, 0, -1);

                        if (logUuids != null) {
                            for (String logUuid : logUuids) {
                                String detailKey = String.format(LogRedisKey.LOG_DETAIL, logUuid);
                                String logJson = redisTemplate.opsForValue().get(detailKey);
                                if (logJson != null) {
                                    MallOperationLog log = JSON.parseObject(logJson, MallOperationLog.class);
                                    // 检查操作者名称或ID是否匹配
                                    if ((log.getOperatorName() != null && log.getOperatorName().contains(request.getOperator())) ||
                                            (log.getOperatorId() != null && log.getOperatorId().contains(request.getOperator()))) {
                                        logs.add(log);
                                    }
                                }
                            }
                        }
                    }
                }

                // 分页
                if (!logs.isEmpty()) {
                    int start = (request.getPage() - 1) * request.getPageSize();
                    int end = Math.min(start + request.getPageSize(), logs.size());
                    if (start < logs.size()) {
                        return logs.subList(start, end);
                    }
                }
            }
        } catch (Exception e) {
            log.error("从Redis查询日志失败", e);
        }
        return null;
    }

    private long getTotalFromRedis(LogController.LogQueryRequest request) {
        try {
            // 对于简单查询，尝试从Redis获取总数
            if (isSimpleQuery(request)) {
                long total = 0;

                // 根据日志类型和日期查询
                if (request.getLogType() != null && request.getStartTime() != null && request.getEndTime() != null) {
                    LocalDate startDate = request.getStartTime().toLocalDate();
                    LocalDate endDate = request.getEndTime().toLocalDate();

                    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                        String dateStr = date.format(DATE_FORMAT);
                        String dateKey = String.format(LogRedisKey.LOG_DATE_INDEX, dateStr, request.getLogType());
                        Long size = redisTemplate.opsForList().size(dateKey);
                        if (size != null) {
                            total += size;
                        }
                    }
                }

                // 按业务模块查询
                else if (request.getBusinessModule() != null && request.getBusinessNo() != null) {
                    String bizKey = String.format(LogRedisKey.LOG_BIZ_LIST, request.getBusinessModule(), request.getBusinessNo());
                    Long size = redisTemplate.opsForList().size(bizKey);
                    if (size != null) {
                        total = size;
                    }
                }

                // 按操作者查询
                else if (request.getOperator() != null) {
                    for (int operatorType = 1; operatorType <= 4; operatorType++) {
                        String operatorKey = String.format(LogRedisKey.LOG_OPERATOR_LIST, operatorType, request.getOperator());
                        Long size = redisTemplate.opsForList().size(operatorKey);
                        if (size != null) {
                            total += size;
                        }
                    }
                }

                return total;
            }
        } catch (Exception e) {
            log.error("从Redis获取日志总数失败", e);
        }
        return 0;
    }

    private boolean isSimpleQuery(LogController.LogQueryRequest request) {
        // 判断是否为简单查询（只包含日志类型、业务模块、操作者、业务单号和时间范围）
        return request.getResultStatus() == null;
    }

    private LogController.LogPageResponse queryLogsFromDatabase(LogController.LogQueryRequest request) {
        QueryWrapper<MallOperationLog> wrapper = buildQueryWrapper(request);

        // 计算总数
        long total = operatorLogMapper.selectCount(wrapper);

        // 分页查询
        IPage<MallOperationLog> page = new Page<>(request.getPage(), request.getPageSize());
        IPage<MallOperationLog> result = operatorLogMapper.selectPage(page, wrapper);

        return buildPageResponse(result.getRecords(), total, request.getPage(), request.getPageSize());
    }

    private QueryWrapper<MallOperationLog> buildQueryWrapper(LogController.LogQueryRequest request) {
        QueryWrapper<MallOperationLog> wrapper = new QueryWrapper<>();

        if (request.getLogType() != null) {
            wrapper.eq("log_type", request.getLogType());
        }

        if (request.getBusinessModule() != null && !request.getBusinessModule().isEmpty()) {
            wrapper.eq("business_module", request.getBusinessModule());
        }

        if (request.getOperator() != null && !request.getOperator().isEmpty()) {
            wrapper.and(w -> w.like("operator_name", request.getOperator()).or().like("operator_id", request.getOperator()));
        }

        if (request.getBusinessNo() != null && !request.getBusinessNo().isEmpty()) {
            wrapper.and(w -> w.like("business_no", request.getBusinessNo()).or().like("business_id", request.getBusinessNo()));
        }

        if (request.getStartTime() != null) {
            wrapper.ge("create_time", request.getStartTime());
        }

        if (request.getEndTime() != null) {
            wrapper.le("create_time", request.getEndTime());
        }

        if (request.getResultStatus() != null) {
            wrapper.eq("result_status", request.getResultStatus());
        }

        // 按时间倒序排序
        wrapper.orderByDesc("create_time");

        return wrapper;
    }

    private LogController.LogPageResponse buildPageResponse(List<MallOperationLog> list, long total, int page, int pageSize) {
        LogController.LogPageResponse response = new LogController.LogPageResponse();
        response.setList(list);
        response.setTotal(total);
        response.setPage(page);
        response.setPageSize(pageSize);
        response.setTotalPages((int) Math.ceil((double) total / pageSize));
        return response;
    }
}
