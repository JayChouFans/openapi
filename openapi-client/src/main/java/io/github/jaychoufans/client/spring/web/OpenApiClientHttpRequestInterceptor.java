package io.github.jaychoufans.client.spring.web;

import io.github.jaychoufans.core.SignUtils;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

public class OpenApiClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Setter
    private String appId = "appId";
    @Setter
    private String appSecret;
    @Setter
    private String priKey;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String bodyStr = new String(body, StandardCharsets.UTF_8);

        String sign = null;
        try {
            sign = SignUtils.sign(bodyStr);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        String nonce = new Random().nextInt(1000) + ""; // 唯一随机数，可以填入流水号
        String timestamp = System.currentTimeMillis() + "";

        // 生成摘要
        String appSign = null;
        try {
            appSign = SignUtils.appSign(appId, appSecret, nonce, sign, timestamp, priKey);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeySpecException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        HttpHeaders headers = request.getHeaders();
        headers.add("appId", appId);
        headers.add("nonce", nonce);
        headers.add("sign", sign);
        headers.add("timestamp", timestamp);
        headers.add("appSign", appSign);

        return execution.execute(request, body);
    }
}
