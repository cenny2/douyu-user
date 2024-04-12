package com.ht.id.generate;

import com.ht.id.generate.service.IdGenerateService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.LinkedList;
import java.util.Set;

/**
 * @ClassName IdGenerateApplication
 * @Description: TODO
 * @Author: Torrey
 * @Date: 2024/4/12 16:03
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
public class IdGenerateApplication implements CommandLineRunner {
    @Resource
    private IdGenerateService service;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(IdGenerateApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        for (int i = 0 ; i< 10001 ; i++){
            Long seqId = service.getUnSeqId(2);
            System.out.println(seqId);
        }
    }
}
