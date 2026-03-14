package org.example.seckillsession.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.common.RestBean;
import org.example.seckillsession.dto.SeckillRequestDTO;
import org.example.seckillsession.service.SeckillService;
import org.example.seckillsession.vo.SeckillProductVO;
import org.example.seckillsession.vo.SeckillResultVO;
import org.example.seckillsession.vo.SeckillSessionVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "秒杀专场")
@RestController
@RequestMapping("/api/seckill")
@RequiredArgsConstructor
public class SeckillController {

    private final SeckillService seckillService;

 //   @ApiOperation("获取秒杀场次列表")
    @GetMapping("/sessions")
    public RestBean<List<SeckillSessionVO>> getSessions() {
        List<SeckillSessionVO> sessions = seckillService.getSessions();
        return RestBean.success(sessions);
    }

//    @ApiOperation("获取某场次的秒杀商品")
    @GetMapping("/products/{sessionId}")
    public RestBean<List<SeckillProductVO>> getProducts(@PathVariable Long sessionId) {
        List<SeckillProductVO> products = seckillService.getProductsBySession(sessionId);
        return RestBean.success(products);
    }

//    @ApiOperation("获取秒杀商品详情")
    @GetMapping("/detail/{seckillId}")
    public RestBean<SeckillProductVO> getDetail(@PathVariable Long seckillId) {
        SeckillProductVO detail = seckillService.getProductDetail(seckillId);
        return RestBean.success(detail);
    }

 //   @ApiOperation("执行秒杀")
    @PostMapping("/execute")
    public RestBean<SeckillResultVO> seckill(@Valid @RequestBody SeckillRequestDTO dto) {
        SeckillResultVO result = seckillService.seckill(dto);
        return RestBean.success(result);
    }

//    @ApiOperation("查询秒杀结果")
    @GetMapping("/result")
    public RestBean<SeckillResultVO> getResult(@RequestParam Long userId,
                                                   @RequestParam Long seckillId) {
        SeckillResultVO result = seckillService.getResult(userId, seckillId);
        return RestBean.success(result);
    }

 //   @ApiOperation("预热秒杀库存（管理员）")
    @PostMapping("/preheat/{seckillId}")
    public RestBean<Void> preheat(@PathVariable Long seckillId) {
        seckillService.preheatSeckillStock(seckillId);
        return RestBean.success(null);
    }
}