package org.example.ordercartservice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "物流简略信息视图对象")
public class LogisticsSimpleVO {

    @Schema(description = "物流公司", example = "顺丰速运")
    private String company;

    @Schema(description = "物流单号", example = "SF1234567890")
    private String number;

    @Schema(description = "物流状态", example = "1")
    private Integer status;

    @Schema(description = "物流状态文字", example = "已发货")
    private String statusText;

    @Schema(description = "最新物流轨迹", example = "快件到达广州中转站")
    private String latestTrace;

    @Schema(description = "预计送达时间")
    private String estimatedTime;
}