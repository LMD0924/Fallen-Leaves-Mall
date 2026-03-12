package org.example.commonbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CommonBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonBackendApplication.class, args);
    }

}
