package io.github.jaychoufans.openapi;

import io.github.jaychoufans.client.spring.web.OpenApiClientHttpRequestInterceptor;
import io.github.jaychoufans.core.AppInfo;
import io.github.jaychoufans.server.DefaultAppStore;
import io.github.jaychoufans.server.KeyGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.NoSuchAlgorithmException;

@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RestTemplateTest {

    @BeforeAll
    public static void init() throws NoSuchAlgorithmException {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("appId");
        appInfo.setAppSecret("appSecret");
        KeyGenerator keyGenerator = new KeyGenerator();
        keyGenerator.generate((pubKey, priKey) -> {
            System.out.println("pubKey:" + pubKey);
            System.out.println("priKey:" + priKey);
            appInfo.setPubKey(pubKey);
            appInfo.setPriKey(priKey);
        });

        DefaultAppStore.addAppInfo(appInfo);
    }

    @Test
    public void test(@Autowired TestRestTemplate restTemplate) {
        String appId = "appId";
        AppInfo appInfo = DefaultAppStore.getAppInfo(appId);
        OpenApiClientHttpRequestInterceptor openApiClientHttpRequestInterceptor = new OpenApiClientHttpRequestInterceptor();
        openApiClientHttpRequestInterceptor.setAppId(appId);
        openApiClientHttpRequestInterceptor.setAppSecret(appInfo.getAppSecret());
        openApiClientHttpRequestInterceptor.setPriKey(appInfo.getPriKey());

        restTemplate.getRestTemplate().getInterceptors().add(openApiClientHttpRequestInterceptor);

        ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        System.out.println(response);
    }

}
