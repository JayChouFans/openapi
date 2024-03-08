package io.github.jaychoufans.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jaychoufans.server.AppDetailsService;
import okhttp3.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OkHttpTest {

	@Autowired
	AppDetailsService appDetailsService;

	@Autowired
	private OkHttpClient okHttpClient;

	@Test
	public void test(@LocalServerPort int port) throws IOException {
		Map<String, Object> data = new HashMap<>();
		data.put("k", "v");

		Request request = new Request.Builder().url("http://localhost:" + port)
				.post(RequestBody.create(
						MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE),
						new ObjectMapper().writeValueAsString(data)))
				.build();

		try (Response response = okHttpClient.newCall(request).execute();) {
			Assertions.assertTrue(response.isSuccessful());
			Assertions.assertEquals(new String(response.body().bytes()), "success");
		}
	}

}
