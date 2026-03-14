package org.example.seckillsession.service;


import org.example.seckillsession.dto.SeckillRequestDTO;
import org.example.seckillsession.vo.SeckillProductVO;
import org.example.seckillsession.vo.SeckillResultVO;
import org.example.seckillsession.vo.SeckillSessionVO;

import java.util.List;

public interface SeckillService {

    /**
     * 获取秒杀场次列表
     */
    List<SeckillSessionVO> getSessions();

    /**
     * 获取某场次的秒杀商品
     */
    List<SeckillProductVO> getProductsBySession(Long sessionId);

    /**
     * 获取秒杀商品详情
     */
    SeckillProductVO getProductDetail(Long seckillId);

    /**
     * 秒杀接口（高并发）
     */
    SeckillResultVO seckill(SeckillRequestDTO dto);

    /**
     * 查询秒杀结果
     */
    SeckillResultVO getResult(Long userId, Long seckillId);

    /**
     * 预热秒杀库存到Redis
     */
    void preheatSeckillStock(Long seckillId);

    /**
     * 更新场次状态（定时任务）
     */
    void updateSessionStatus();
}