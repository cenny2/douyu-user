package com.ht.user.api;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @ClassName ApiWebApplication
 * @Description: TODO
 * @Author: Torrey
 * @Date: 2024/2/29 21:22
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class ApiWebApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ApiWebApplication.class);
        application.setWebApplicationType(WebApplicationType.SERVLET);
        application.run(args);
    }
}
