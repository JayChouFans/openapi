package io.github.jaychoufans.openapi;

import io.github.jaychoufans.openapi.server.EnableOpenApi;
import io.github.jaychoufans.openapi.server.OpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableOpenApi
@RestController
@RequestMapping
@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @OpenApi
    @RequestMapping
    public String index() {
        return "success";
    }

}