package org.example.seckillsession;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {
        "org.example.ordercartservice.client",
        "org.example.seckillsession.client"
})
public class SeckillSessionApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillSessionApplication.class, args);
    }

}
