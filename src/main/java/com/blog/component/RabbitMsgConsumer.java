package com.blog.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

//ramq消息消费者
@Component
@Slf4j
public class RabbitMsgConsumer {

    //监听队列DataCacheQueue
    @RabbitListener(queues = "DataCacheQueue")
    public void dataCacheProcess(String testMessage) {
        log.info("DataCacheQueue消费者收到消息  {}" , testMessage);
    }

    //监听队列UserForbidQueue
    @RabbitListener(queues = "UserForbidReadyQueue")
    public void userForbidProcess(String testMessage) {
        log.info("UserForbidQueue消费者收到消息  {}" , testMessage);
    }

    //监听队列LoginFailQueue
    @RabbitListener(queues = "LoginFailReadyQueue")
    public void loginFailProcess(String testMessage) {
        log.info("LoginFailQueue消费者收到消息  {}" , testMessage);

    }

    //监听队列BlogPublishQueue
    @RabbitListener(queues = "BlogPublishReadyQueue")
    public void blogPublishProcess(String testMessage) {
        log.info("blogPublishProcess消费者收到消息  {}" , testMessage);
    }
}
