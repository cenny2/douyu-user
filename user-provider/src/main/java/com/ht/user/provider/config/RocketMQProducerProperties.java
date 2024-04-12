package com.ht.user.provider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName RocketMQProducerProperties
 * @Description: mq生产者属性配置类
 * @Author: Torrey
 * @Date: 2024/4/10 10:32
 **/


@ConfigurationProperties(prefix = "ht.mq.producer")
@Configuration
public class RocketMQProducerProperties {

    //消费者nameserver地址
    private String nameServer;
    //分组名称
    private String groupName;
    //消息重发次数
    private int retryTimes;
    //消息发送超时时间
    private int sendTimeOut;

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

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public int getSendTimeOut() {
        return sendTimeOut;
    }

    public void setSendTimeOut(int sendTimeOut) {
        this.sendTimeOut = sendTimeOut;
    }

    @Override
    public String toString() {
        return "RocketMQProducerProperties{" +
                "nameServer='" + nameServer + '\'' +
                ", groupName='" + groupName + '\'' +
                ", retryTimes=" + retryTimes +
                ", sendTimeOut=" + sendTimeOut +
                '}';
    }
}
