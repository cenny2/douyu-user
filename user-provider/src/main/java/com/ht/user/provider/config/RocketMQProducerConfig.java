package com.ht.user.provider.config;

import jakarta.annotation.Resource;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @ClassName RocketMQProducerConfig
 * @Description: 生产者启动配置
 * @Author: Torrey
 * @Date: 2024/4/10 10:37
 **/
@Configuration
public class RocketMQProducerConfig {

    public static final Logger LOGGER = LoggerFactory.getLogger(RocketMQProducerConfig.class);

    @Resource
    private RocketMQProducerProperties rocketMQProducerProperties;

    @Value("${spring.application.name}")
    private String applicationName;


    @Bean
    public MQProducer mqProducer(){
        //设置异步线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setName(applicationName + " - rmq-producer :" + ThreadLocalRandom.current().nextInt(1000));
                return thread;
            }
        });
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer("producer-group-test");
        defaultMQProducer.setNamesrvAddr(rocketMQProducerProperties.getNameServer());
        defaultMQProducer.setSendMsgTimeout(rocketMQProducerProperties.getSendTimeOut());
        defaultMQProducer.setProducerGroup(rocketMQProducerProperties.getGroupName());
        defaultMQProducer.setRetryTimesWhenSendFailed(rocketMQProducerProperties.getRetryTimes());
        defaultMQProducer.setRetryTimesWhenSendAsyncFailed(rocketMQProducerProperties.getRetryTimes());
        defaultMQProducer.setRetryAnotherBrokerWhenNotStoreOK(true);
        //设置异步发送线程池
        defaultMQProducer.setAsyncSenderExecutor(executorService);
        try {
            defaultMQProducer.start();
            LOGGER.info("mq生产者启动成功,nameSrv is {}", rocketMQProducerProperties.getNameServer());
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        }
        return defaultMQProducer;
    }


}
