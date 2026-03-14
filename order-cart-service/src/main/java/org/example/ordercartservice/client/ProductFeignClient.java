package org.example.ordercartservice.client;


import org.example.ordercartservice.dto.DeductStockDTO;
import org.example.ordercartservice.vo.ProductSkuVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "product-service", path = "/api/product")
public interface ProductFeignClient {

    @GetMapping("/sku/info")
    ProductSkuVO getSkuInfo(@RequestParam("skuId") Long skuId);

    @PostMapping("/sku/batchInfo")
    Map<Long, ProductSkuVO> batchGetSkuInfo(@RequestBody List<Long> skuIds);

    @PostMapping("/stock/deduct")
    Boolean deductStock(@RequestBody DeductStockDTO dto);

    @PostMapping("/stock/restore")
    Boolean restoreStock(@RequestParam("orderNo") String orderNo);
}