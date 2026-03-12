package org.example.merchantbackend.client;

import org.example.backend.common.RestBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/*
 * @Author:总会落叶
 * @Date:2026/3/12
 * @Description:
 */
@FeignClient(name = "FallenLeavesMall-User",url="http://localhost:8080")
public interface ItemClient {
    @GetMapping("api/user/getUserId")
    Long getUserId();
}
