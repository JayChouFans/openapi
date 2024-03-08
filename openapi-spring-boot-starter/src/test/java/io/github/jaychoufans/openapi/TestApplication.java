package io.github.jaychoufans.openapi;

import io.github.jaychoufans.client.apache.OpenApiHttpRequestInterceptor;
import io.github.jaychoufans.client.okhttp3.OpenApiInterceptor;
import io.github.jaychoufans.core.AppDetails;
import io.github.jaychoufans.openapi.autoconfigure.EnableOpenApi;
import io.github.jaychoufans.server.AppDetailsService;
import io.github.jaychoufans.server.InMemoryAppDetailsService;
import io.github.jaychoufans.server.KeyGenerator;
import io.github.jaychoufans.server.OpenApi;
import io.github.jaychoufans.server.spring.web.OpenApiHandlerInterceptor;
import okhttp3.OkHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@EnableOpenApi
@RestController
@RequestMapping
@SpringBootApplication
@Import(value = { OpenApiHandlerInterceptor.class })
public class TestApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}

	@OpenApi
	@RequestMapping
	public String index() {
		return "success";
	}

	@Bean
	public OkHttpClient okHttpClient(AppDetailsService appDetailsService) {
		String appId = "appId";
		AppDetails appDetails = appDetailsService.loadByAppId(appId);
		OpenApiInterceptor openApiInterceptor = new OpenApiInterceptor();
		openApiInterceptor.setAppId(appId);
		openApiInterceptor.setAppSecret(appDetails.getAppSecret());
		openApiInterceptor.setPriKey(appDetails.getPriKey());

		return new OkHttpClient.Builder().addInterceptor(openApiInterceptor).build();
	}

	@Bean
	public HttpClient httpClient(AppDetailsService appDetailsService) {
		String appId = "appId";
		AppDetails appDetails = appDetailsService.loadByAppId(appId);
		OpenApiHttpRequestInterceptor openApiInterceptor = new OpenApiHttpRequestInterceptor();
		openApiInterceptor.setAppId(appId);
		openApiInterceptor.setAppSecret(appDetails.getAppSecret());
		openApiInterceptor.setPriKey(appDetails.getPriKey());

		return HttpClientBuilder.create().addInterceptorLast(openApiInterceptor).build();
	}

	@Bean
	public AppDetailsService appDetailsService() throws NoSuchAlgorithmException {
		InMemoryAppDetailsService appDetailsService = new InMemoryAppDetailsService();
		AppDetails appDetails = new AppDetails();
		appDetails.setAppId("appId");
		appDetails.setAppSecret("appSecret");
		KeyGenerator keyGenerator = new KeyGenerator();
		keyGenerator.generate((pubKey, priKey) -> {
			System.out.println("pubKey:" + pubKey);
			System.out.println("priKey:" + priKey);
			appDetails.setPubKey(pubKey);
			appDetails.setPriKey(priKey);
		});

		appDetailsService.saveApp(appDetails);
		return appDetailsService;
	}

}