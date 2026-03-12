package org.example.merchantbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.common.RestBean;
import org.example.commonbackend.code.MerchantStatus;
import org.example.merchantbackend.client.ItemClient;
import org.example.merchantbackend.controller.VO.MerchantControllerVO;
import org.example.merchantbackend.entity.Merchant;
import org.example.merchantbackend.entity.dto.AuditMerchantDTO;
import org.example.merchantbackend.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/*
 * @Author:总会落叶
 * @Date:2026/2/9
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("api/merchant")
@Tag(name = "商家", description = "商家相关接口")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ItemClient itemClient;
    /*
    * 申请为商家
    * */
    @PostMapping("/applyMerchant")
    public RestBean<String> applyMerchant(@RequestBody Merchant merchant) {
        if(merchant.getUserId() == null) return RestBean.failure("申请状态不合法，用户ID不能为空");
        if(merchantService.applyMerchant(merchant) == 1) return RestBean.success("申请成功,等待审核");
        return RestBean.failure("申请失败");
    }

    /**
     *管理员审核
     *
     *
     */
    @PostMapping("/adminAuditMerchant")
    public RestBean<String> adminAuditMerchant(@RequestBody AuditMerchantDTO dto) {
        Long adminId = dto.getAdminId();
        log.info("userId:{}, dto:{}", adminId, dto);
        if(adminId == null || adminId!=1) return RestBean.failure("暂无权限");

        // 将数字状态转换为枚举
       // MerchantStatus status = MerchantStatus.fromCode(dto.getStatus());

        if(merchantService.adminAuditMerchant(dto) == 1)
            return RestBean.success("审核成功");
        return RestBean.failure("审核失败");
    }



    /*
    *查询申请进度
    *  */
    @GetMapping("/queryApplyProgress")
    public RestBean<List<MerchantControllerVO>> queryApplyProgress(@RequestParam("userId") Long userId) {
        if (userId == null) {
            return RestBean.failure("获取失败,用户ID为空");
        }
        Merchant merchant = new Merchant();
        merchant.setUserId(userId);
        List<MerchantControllerVO> merchants =  merchantService.selectMerchant(merchant,userId);
        return RestBean.success(merchants);
    }


    /*
    * 查询商家
    * */
    @PostMapping("/selectMerchant")
    public RestBean<List<MerchantControllerVO>> selectMerchant(@RequestBody Merchant merchant,
                                                   @RequestParam("userId") Long userId) {
        if(merchant == null || userId == null) return RestBean.failure("暂无权限");
        List<MerchantControllerVO> merchants = merchantService.selectMerchant(merchant,userId);
        return RestBean.success(merchants);
    }

    /*
    * 根据ID查询商家详情
    * */
    @GetMapping("/selectMerchantById/{id}")
    public RestBean<MerchantControllerVO> selectMerchantById(@PathVariable("id") Long id,
                                                      @RequestParam("userId") Long userId) {
        if(id == null || userId == null) return RestBean.failure("参数不合法");
        Merchant merchant = new Merchant();
        merchant.setId(id);
        List<MerchantControllerVO> merchants = merchantService.selectMerchant(merchant,userId);
        if(merchants == null || merchants.isEmpty()) return RestBean.failure("商家不存在");
        return RestBean.success(merchants.get(0));
    }

    /*
    * 根据状态查询商家
    * */
    @GetMapping("/selectMerchantByStatus")
    public RestBean<List<MerchantControllerVO>> selectMerchantByStatus(@RequestParam("status") MerchantStatus status,
                                                             @RequestParam("userId") Long userId) {
        if(status == null || userId == null) return RestBean.failure("参数不合法");
        Merchant merchant = new Merchant();
        merchant.setStatus(status);
        List<MerchantControllerVO> merchants = merchantService.selectMerchant(merchant,userId);
        return RestBean.success(merchants);
    }

    /*
    * 模糊搜索商家
    * */
    @GetMapping("/searchMerchant")
    public RestBean<List<MerchantControllerVO>> searchMerchant(@RequestParam("keyword") String keyword,
                                                      @RequestParam("userId") Long userId) {
        if(keyword == null || userId == null) return RestBean.failure("参数不合法");
        Merchant merchant = new Merchant();
        merchant.setMerchantName(keyword);
        List<MerchantControllerVO> merchants = merchantService.selectMerchant(merchant,userId);
        return RestBean.success(merchants);
    }

    /*
    * 修改商家信息
    * */
    @PostMapping("/updateMerchant")
    public RestBean<String> updateMerchant(@RequestBody Merchant merchant, @RequestParam("userId") Long userId) {
        log.info("userId:{}", userId);
        Long merchantId = merchant.getUserId();
        if(userId == null || !Objects.equals(userId, merchantId)) return RestBean.failure("暂无权限");
        if(merchantService.updateMerchant(merchant) > 0) return RestBean.success("修改成功");
        return RestBean.failure("修改失败");
    }

    /*
    * 逻辑删除商家（仅管理员/测试员）
    * */
    @DeleteMapping("/deleteMerchant/{id}")
    public RestBean<String> deleteMerchant(@PathVariable("id") Long id,
                                           @RequestParam("userId") Long userId) {
        if (id == null || userId == null) return RestBean.failure("参数不合法");
        try {
            if (merchantService.deleteMerchant(id, userId) == 1) return RestBean.success("删除成功");
            return RestBean.failure("删除失败");
        } catch (Exception e) {
            return RestBean.failure(e.getMessage());
        }
    }

    //获取商家总数
    @GetMapping("/getMerchantCount")
    public RestBean<Long> getMerchantCount(){
        return RestBean.success(merchantService.getMerchantCount());
    }

    //今日新增商家
    @GetMapping("/getTodayMerchantCount")
    public RestBean<Long> getTodayMerchantCount(){
        return RestBean.success(merchantService.getTodayMerchantCount());
    }

}
