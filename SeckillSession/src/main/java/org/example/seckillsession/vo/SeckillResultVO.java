package org.example.seckillsession.vo;

import lombok.Data;

@Data
public class SeckillResultVO {
    private Boolean success;
    private String message;
    private Long seckillId;
    private Long orderId;
    private String orderNo;
    private String token;  // 用于查询结果

    public static SeckillResultVO success() {
        SeckillResultVO vo = new SeckillResultVO();
        vo.setSuccess(true);
        return vo;
    }

    public static SeckillResultVO fail(String message) {
        SeckillResultVO vo = new SeckillResultVO();
        vo.setSuccess(false);
        vo.setMessage(message);
        return vo;
    }
}