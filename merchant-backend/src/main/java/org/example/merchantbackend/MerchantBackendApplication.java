package org.example.merchantbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan({
        "org.example.merchantbackend.mapper",
        "org.example.backend.mapper"
})
@SpringBootApplication
public class MerchantBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MerchantBackendApplication.class, args);
    }

}
