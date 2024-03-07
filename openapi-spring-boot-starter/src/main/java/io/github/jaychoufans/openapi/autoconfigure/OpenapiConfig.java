package io.github.jaychoufans.openapi.autoconfigure;

import io.github.jaychoufans.server.spring.web.OpenApiHandlerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class OpenapiConfig extends WebMvcConfigurationSupport {

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new OpenApiHandlerInterceptor())
                .addPathPatterns("/**");
    }

}
