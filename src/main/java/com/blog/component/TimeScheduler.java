package com.blog.component;

import com.blog.service.LikesService;
import com.blog.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//用于定时将Redis的相关数据存至mysql数据库的任务管理器
@Component
@EnableScheduling
@Slf4j
public class TimeScheduler {

    @Resource
    private LikesService likesService;

    //每一分钟存储一次点赞数据(秒数为0时触发)
    @Scheduled(cron = "0-0 0/1 * * * ?")
    public void LikeStoreTask(){
        log.info("开始执行点赞数据存储任务……");
        //发送请求
        likesService.storeLike();
        log.info("点赞数据存储任务已完成");
    }
}
