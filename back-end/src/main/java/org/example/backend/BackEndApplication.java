package org.example.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 用户端后端启动类
 * 同时加载 common-backend 中的日志、Redis 等公共组件
 */
@SpringBootApplication(
        scanBasePackages = {
                "org.example.backend",
                "org.example.commonbackend"
        }
)
@MapperScan({
        "org.example.backend.mapper",
        "org.example.commonbackend.mapper"
})
@EnableAsync
public class BackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackEndApplication.class, args);
    }

}
