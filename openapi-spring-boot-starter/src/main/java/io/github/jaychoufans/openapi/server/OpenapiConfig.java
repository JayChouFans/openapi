package io.github.jaychoufans.openapi.server;

import io.github.jaychoufans.openapi.core.SignUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class OpenapiConfig extends WebMvcConfigurationSupport {

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                OpenApi openApi = handlerMethod.getBeanType().getAnnotation(OpenApi.class);
                if (openApi == null) {
                    openApi = handlerMethod.getMethodAnnotation(OpenApi.class);
                }

                // 属于OpenApi
                if (openApi != null) {
                    String sign = SignUtils.sign("{}");
                    if (!sign.equals(request.getHeader("sign"))) {
                        throw new RuntimeException("sign不正确");
                    }

                    String appId = request.getHeader("appId");
                    AppInfo appInfo = DefaultAppStore.getAppInfo(appId);
                    String appSecret = appInfo.getAppSecret();
                    String nonce = request.getHeader("nonce");
                    String timestamp = request.getHeader("timestamp");

                    String appSign = request.getHeader("appSign");
                    if (!SignUtils.verifyAppSign(appId, appSecret, nonce, sign, timestamp, appSign, appInfo.getPubKey())) {
                        throw new RuntimeException("appSign不正确");
                    }

                }

                return HandlerInterceptor.super.preHandle(request, response, handler);
            }
        }).addPathPatterns("/**");
    }

}
