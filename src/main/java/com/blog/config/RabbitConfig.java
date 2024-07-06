package com.blog.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {

    //数据缓冲cache队列
    @Bean
    public Queue DataCacheQueue() {
        return new Queue("DataCacheQueue",true);
    }
    @Bean
    DirectExchange MailCacheExchange() {
        return new DirectExchange("MailCacheExchange");
    }
    @Bean
    DirectExchange CommentCacheExchange() {
        return new DirectExchange("CommentCacheExchange");
    }
    @Bean
    Binding MailCacheBinding() {
        return BindingBuilder.bind(DataCacheQueue()).to(MailCacheExchange()).with("MailCacheRouting");
    }
    @Bean
    Binding CommentCacheBinding() {
        return BindingBuilder.bind(DataCacheQueue()).to(CommentCacheExchange()).with("CommentCacheRouting");
    }

    //失败封禁延时delay队列
    @Bean
    public Queue UserForbidQueue() {
        Map<String, Object> args = new HashMap<>(2);
        // x-dead-letter-exchange    这里声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", "ForbidExchange");
        // x-dead-letter-routing-key  这里声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", "UserForbidReadyRouting");
        return QueueBuilder.durable("UserForbidQueue").withArguments(args).build();
    }
    @Bean
    DirectExchange ForbidExchange() {
        return new DirectExchange("ForbidExchange");
    }
    @Bean
    Binding UserForbidBinding() {return BindingBuilder.bind(UserForbidQueue()).to(ForbidExchange()).with("UserForbidRouting");}
    //死信队列和其routing
    @Bean
    public Queue UserForbidReadyQueue() {
        return new Queue("UserForbidReadyQueue",true);
    }
    @Bean
    Binding UserForbidReadyBinding() {return BindingBuilder.bind(UserForbidReadyQueue()).to(ForbidExchange()).with("UserForbidReadyRouting");}

    //取消封禁队列与交换机
    @Bean
    public Queue CancelForbidQueue() {
        return new Queue("CancelForbidQueue",true);
    }
    @Bean
    DirectExchange CancelExchange() {return new DirectExchange("CancelExchange");}
    @Bean
    Binding CancelForbidBinding() {return BindingBuilder.bind(CancelForbidQueue()).to(CancelExchange()).with("CancelForbidRouting");}


    //延时博客publish队列
    @Bean
    public Queue BlogPublishQueue() {
        Map<String, Object> args = new HashMap<>(2);
        // x-dead-letter-exchange    这里声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", "BlogPublishExchange");
        // x-dead-letter-routing-key  这里声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", "BlogPublishReadyRouting");
        return QueueBuilder.durable("BlogPublishQueue").withArguments(args).build();
    }
    @Bean
    DirectExchange BlogPublishExchange() {
        return new DirectExchange("BlogPublishExchange");
    }
    @Bean
    Binding BlogPublishBinding() {return BindingBuilder.bind(BlogPublishQueue()).to(BlogPublishExchange()).with("BlogPublishRouting");}
    //死信队列和其routing
    @Bean
    public Queue BlogPublishReadyQueue() {
        return new Queue("BlogPublishReadyQueue",true);
    }
    @Bean
    Binding BlogPublishReadyBinding() {return BindingBuilder.bind(BlogPublishReadyQueue()).to(BlogPublishExchange()).with("BlogPublishReadyRouting");}
}