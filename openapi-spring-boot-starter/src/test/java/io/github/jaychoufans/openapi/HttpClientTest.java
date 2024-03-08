package io.github.jaychoufans.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jaychoufans.server.AppDetailsService;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpClientTest {

	@Autowired
	AppDetailsService appDetailsService;

	@Autowired
	HttpClient httpClient;

	@Test
	public void test(@LocalServerPort int port) throws IOException {
		Map<String, Object> data = new HashMap<>();
		data.put("k", "v");

		HttpPost httpPost = new HttpPost("http://localhost:" + port);
		StringEntity stringEntity = new StringEntity(new ObjectMapper().writeValueAsString(data));
		stringEntity.setContentEncoding(StandardCharsets.UTF_8.name());
		stringEntity.setContentType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
		httpPost.setEntity(stringEntity);

		HttpResponse response = httpClient.execute(httpPost);

		Assertions.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
		Assertions.assertEquals(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8), "success");
	}

}
