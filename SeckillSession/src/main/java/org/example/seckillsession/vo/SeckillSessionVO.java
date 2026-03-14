package org.example.seckillsession.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class SeckillSessionVO {
    private Long id;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;  // 0未开始 1进行中 2已结束
    private String statusText;

    public String getStartTimeStr() {
        return startTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String getEndTimeStr() {
        return endTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String getCountdown() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) {
            // 距离开始还有多久
            long seconds = java.time.Duration.between(now, startTime).getSeconds();
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            long secs = seconds % 60;
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        } else if (now.isBefore(endTime)) {
            return "秒杀中";
        } else {
            return "已结束";
        }
    }
}