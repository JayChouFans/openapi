package io.github.jaychoufans.openapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RestTemplateTest {

    @Test
    public void test(@Autowired TestRestTemplate restTemplate) {
        String response = restTemplate.getForObject("/", String.class);
        System.out.println(response);
    }

}
