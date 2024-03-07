package io.github.jaychoufans.server.spring.web;

import io.github.jaychoufans.core.AppInfo;
import io.github.jaychoufans.core.SignUtils;
import io.github.jaychoufans.server.DefaultAppStore;
import io.github.jaychoufans.server.OpenApi;
import org.springframework.util.StreamUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

public class OpenApiHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        OpenApi openApi = handlerMethod.getBeanType().getAnnotation(OpenApi.class);
        if (openApi == null) {
            openApi = handlerMethod.getMethodAnnotation(OpenApi.class);
        }

        // 属于OpenApi
        if (openApi != null) {

            String bodyStr = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

            String sign = SignUtils.sign(bodyStr);
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

}
