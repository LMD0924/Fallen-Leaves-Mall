package org.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
/*
 * @Author:总会落叶
 * @Date:2026/2/5
 * @Description:全局跨域配置
 */

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 1. 允许的前端地址（Vue开发服务器）
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://127.0.0.1:5173");

        // 2. 允许的请求头
        config.addAllowedHeader("*");

        // 3. 允许的请求方法
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("PATCH");

        // 4. 允许携带凭证（cookie、token等）
        config.setAllowCredentials(true);

        // 5. 预检请求的缓存时间（秒）
        config.setMaxAge(3600L);

        // 6. 暴露的响应头
        config.addExposedHeader("token");
        config.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有接口应用跨域配置
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}