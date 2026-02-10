package org.example.merchantbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.common.RestBean;
import org.example.merchantbackend.entity.Merchant;
import org.example.merchantbackend.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
import java.util.List;

/*
 * @Author:总会落叶
 * @Date:2026/2/9
 * @Description:
 */
@RestController
@RequestMapping("api/merchant")
@Tag(name = "商家", description = "商家相关接口")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    /*
    * 申请为商家
    * */
    @PostMapping("/applyMerchant")
    public RestBean<String> applyMerchant(@RequestBody Merchant merchant) {
        if(merchant.getUserId() == null) return RestBean.failure("申请状态不合法，用户ID不能为空");
        if(merchantService.applyMerchant(merchant) == 1) return RestBean.success("申请成功");
        return RestBean.failure("申请失败");
    }

    /*
    *查询申请进度
    *  */
    @GetMapping("/queryApplyProgress")
    public RestBean<Integer> queryApplyProgress(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("id");
        if (userId == null) {
            return RestBean.failure("获取失败,用户ID为空");
        }
        Merchant merchant = merchantService.queryApplyProgress(userId);
        if(merchant.getStatus() == 2) {
            //申请失败返回失败原因
            return RestBean.failure(merchant.getRejectReason());
        }
        return RestBean.success(merchant.getStatus());
    }


    /*
    * 查询商家
    * */
    @GetMapping("/selectMerchant")
    public RestBean<List<Merchant>> selectMerchant(@RequestBody Merchant merchant,
                                                   @RequestParam("id") Long userId) {
        if(merchant == null || userId == null) return RestBean.failure("暂无权限");
        if(merchantService.selectMerchant(merchant,userId) == null) return RestBean.failure("暂无权限");
        return RestBean.success(merchantService.selectMerchant(merchant,userId));
    }

    /*
    * 修改商家信息
    * */
    @PostMapping("/updateMerchant")
    public RestBean<String> updateMerchant(@RequestBody Merchant merchant,
                                           @RequestParam("id") Long userId) {
        if(userId == null) return RestBean.failure("暂无权限");
        if(merchantService.updateMerchant(merchant,userId) != 1) return RestBean.failure("修改失败");
        return RestBean.success("修改成功");
    }
}
