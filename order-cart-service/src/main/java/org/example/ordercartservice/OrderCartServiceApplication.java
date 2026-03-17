package org.example.ordercartservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class OrderCartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderCartServiceApplication.class, args);
    }

}
