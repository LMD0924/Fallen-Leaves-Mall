package org.example.merchantbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@MapperScan({
        "org.example.merchantbackend.mapper",
        "org.example.backend.mapper",
        "org.example.couponservice.mapper",
        "org.example.productservice.mapper",
})
@EnableFeignClients
@SpringBootApplication
public class MerchantBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MerchantBackendApplication.class, args);
    }

}
