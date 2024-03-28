package com.ht.user.provider;

import org.apache.catalina.User;
import
        org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import
        org.springframework.boot.autoconfigure.SpringBootApplication;
import
        org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.Scanner;

/**
 * @ClassName UserProviderApplication
 * @Description: TODO
 * @Author: Torrey
 * @Date: 2024/2/28 21:29
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
public class UserProviderApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(UserProviderApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }
}
