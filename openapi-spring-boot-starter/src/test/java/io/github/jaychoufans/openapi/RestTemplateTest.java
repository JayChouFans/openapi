package io.github.jaychoufans.openapi;

import io.github.jaychoufans.client.spring.web.OpenApiClientHttpRequestInterceptor;
import io.github.jaychoufans.core.AppDetails;
import io.github.jaychoufans.server.AppDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RestTemplateTest {

	@Autowired
	AppDetailsService appDetailsService;

	@Test
	public void test(@Autowired TestRestTemplate restTemplate) {
		String appId = "appId";
		AppDetails appDetails = appDetailsService.loadByAppId(appId);
		OpenApiClientHttpRequestInterceptor openApiClientHttpRequestInterceptor = new OpenApiClientHttpRequestInterceptor();
		openApiClientHttpRequestInterceptor.setAppId(appId);
		openApiClientHttpRequestInterceptor.setAppSecret(appDetails.getAppSecret());
		openApiClientHttpRequestInterceptor.setPriKey(appDetails.getPriKey());

		restTemplate.getRestTemplate().getInterceptors().add(openApiClientHttpRequestInterceptor);

		ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
		Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
		Assertions.assertEquals(response.getBody(), "success");
	}

}
