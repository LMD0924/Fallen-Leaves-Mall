//package org.example.backend.config;
//
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Contact;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.info.License;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///*
// * @Author:总会落叶
// * @Date:2026/2/5
// * @Description: OpenAPI 配置
// */
//
//@Configuration
//public class OpenApiConfig {
//
//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//                .info(new Info()
//                        .title("落叶商城 API")
//                        .version("1.0")
//                        .description("落叶商城后端服务接口文档")
//                        .contact(new Contact()
//                                .name("总会落叶")
//                                .url("https://github.com/LMD0924")
//                                .email("1545204857@qq.com"))
//                        .license(new License()
//                                .name("仅供开发测试使用")
//                                .url("")));
//    }
//}