package org.example.commonbackend.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commonbackend.code.LogType;
import org.example.commonbackend.entity.MallOperationLog;
import org.example.commonbackend.service.Impl.LogQueryServiceImpl;
import org.example.commonbackend.service.IOperationLogService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/log")
@RequiredArgsConstructor
public class LogController {

    private final LogQueryServiceImpl logQueryService;
    private final IOperationLogService operationLogService;
    private final RedisTemplate<String, String> redisTemplate;

    @Data
    public static class LogQueryRequest {
        private Integer logType;
        private String businessModule;
        private String operator;
        private String businessNo;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer resultStatus;
        private Integer page = 1;
        private Integer pageSize = 10;
    }

    @Data
    public static class LogStatResponse {
        private long todayCount;
        private long userOperCount;
        private long orderOperCount;
        private long failCount;
    }

    @Data
    public static class LogPageResponse {
        private List<MallOperationLog> list;
        private long total;
        private int page;
        private int pageSize;
        private int totalPages;
    }

    @PostMapping("/list")
    public ResponseEntity<LogPageResponse> getLogList(@RequestBody LogQueryRequest request) {
        try {
            LogPageResponse response = logQueryService.queryLogs(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取日志列表失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/detail/{logUuid}")
    public ResponseEntity<MallOperationLog> getLogDetail(@PathVariable String logUuid) {
        try {
            MallOperationLog log = logQueryService.getLogByUuid(logUuid);
            return log != null ? ResponseEntity.ok(log) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("获取日志详情失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<LogStatResponse> getLogStatistics() {
        try {
            LogStatResponse stats = logQueryService.getLogStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("获取日志统计失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportLogs(@RequestBody LogQueryRequest request) {
        try {
            ByteArrayOutputStream outputStream = logQueryService.exportLogs(request);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "logs.xlsx");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("导出日志失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/batch-delete")
    public ResponseEntity<Map<String, Boolean>> batchDeleteLogs(@RequestBody List<String> logUuids) {
        try {
            boolean success = logQueryService.batchDeleteLogs(logUuids);
            return ResponseEntity.ok(Map.of("success", success));
        } catch (Exception e) {
            log.error("批量删除日志失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete/{logUuid}")
    public ResponseEntity<Map<String, Boolean>> deleteLog(@PathVariable String logUuid) {
        try {
            boolean success = logQueryService.deleteLog(logUuid);
            return ResponseEntity.ok(Map.of("success", success));
        } catch (Exception e) {
            log.error("删除日志失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
