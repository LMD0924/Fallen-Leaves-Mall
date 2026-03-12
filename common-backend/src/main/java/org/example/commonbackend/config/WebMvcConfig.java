package org.example.commonbackend.config;

import org.example.commonbackend.interceptor.LogContextInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * @Author:总会落叶
 * @Date:2026/3/3
 * @Description:
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LogContextInterceptor logContextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注意：这个拦截器要在你的认证拦截器之后执行
        registry.addInterceptor(logContextInterceptor)
                .addPathPatterns("/**")
                .order(1);  // 设置顺序
    }

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射 /images/** 路径到本地上传目录
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
