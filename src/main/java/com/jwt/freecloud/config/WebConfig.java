package com.jwt.freecloud.config;

import com.jwt.freecloud.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author：揭文滔
 * @since：2023/3/2
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 拦截所有的请求
                .allowedOrigins("*")  // 可跨域的域名，可以为 *
                .allowCredentials(true) // 是否发送Cookie
                .allowedMethods("GET", "POST", "DELETE", "PUT")   // 允许跨域的方法，可以单独配置
                .maxAge(3600)
                .allowedHeaders("*");  // 允许跨域的请求头，可以单独配置

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**");
    }
}

