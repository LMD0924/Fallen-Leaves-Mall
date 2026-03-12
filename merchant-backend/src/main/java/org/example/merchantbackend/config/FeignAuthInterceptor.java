package org.example.merchantbackend.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.util.RequestContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
@Slf4j
@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String authHeader = extractAuthHeaderFromCurrentRequest();

        if (authHeader != null) {
            // 直接传递完整的 Authorization header
            template.header("Authorization", authHeader);
            log.info("✅ Added Authorization header to Feign request: {}", authHeader);
        } else {
            log.warn("❌ No Authorization header found in current request");
        }
    }

    private String extractAuthHeaderFromCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader("Authorization");  // 返回完整的 "Bearer xxx"
        }
        return null;
    }
}