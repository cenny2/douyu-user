package com.ht.user.provider.config;

import com.alibaba.fastjson.JSON;
import com.ht.user.dto.UserDTO;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @ClassName RocketMQConsumerConfig
 * @Description: 消费者启动配置
 * @Author: Torrey
 * @Date: 2024/4/10 10:36
 **/
@Configuration
public class RocketMQConsumerConfig implements InitializingBean {

    public static final Logger LOGGER = LoggerFactory.getLogger(RocketMQConsumerConfig.class);
    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;

    /**
     * 初始化消费者方法
     */
    public void initConsumerConfig(){
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer("consumer-group-test");
        defaultMQPushConsumer.setVipChannelEnabled(false);
        defaultMQPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameServer());
        defaultMQPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName());
        defaultMQPushConsumer.setConsumeMessageBatchMaxSize(1);
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        try {
            defaultMQPushConsumer.subscribe("userCache", "*");
            defaultMQPushConsumer.setMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    System.out.println("消费了消息："+ JSON.parseObject(msgs.get(0).getBody(), UserDTO.class));
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });

            defaultMQPushConsumer.start();
            LOGGER.info("MQ消费者已经启动成功！ nameServer is {}",rocketMQConsumerProperties.getNameServer());
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * InitializingBean：初始化方法后回调
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        initConsumerConfig();
    }
}
