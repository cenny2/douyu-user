package com.ht.user.provider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName RocketMQConsumerProperties
 * @Description: 消费者配置类
 * @Author: Torrey
 * @Date: 2024/4/10 10:57
 **/
@ConfigurationProperties(prefix = "ht.mq.consumer")
@Configuration
public class RocketMQConsumerProperties {

    private String nameServer;

    private String groupName;

    public String getNameServer() {
        return nameServer;
    }

    public void setNameServer(String nameServer) {
        this.nameServer = nameServer;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "RocketMQConsumerProperties{" +
                "nameServer='" + nameServer + '\'' +
                ", groupName='" + groupName + '\'' +
                '}';
    }
}
