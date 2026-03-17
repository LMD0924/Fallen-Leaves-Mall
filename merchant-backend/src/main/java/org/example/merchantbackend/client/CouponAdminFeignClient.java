package org.example.merchantbackend.client;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.backend.common.RestBean;
import org.example.couponservice.vo.CouponVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/*
 * @Author:总会落叶
 * @Date:2026/3/14
 * @Description:
 */
// back-end 中的 Feign 客户端
@FeignClient(name = "merchant-service", path = "/merchant/api/admin/coupon")
public interface CouponAdminFeignClient {

    @GetMapping("/list")
    RestBean<Page<CouponVO>> getCouponList(@RequestParam Integer pageNum,
                                           @RequestParam Integer pageSize);
}