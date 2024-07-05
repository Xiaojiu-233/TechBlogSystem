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
        args.put("x-dead-letter-exchange", "ForbidAndLoginExchange");
        // x-dead-letter-routing-key  这里声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", "UserForbidReadyBinding");
        return QueueBuilder.durable("UserForbidQueue").withArguments(args).build();
    }
    @Bean
    public Queue LoginFailQueue() {
        Map<String, Object> args = new HashMap<>(2);
        // x-dead-letter-exchange    这里声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", "ForbidAndLoginExchange");
        // x-dead-letter-routing-key  这里声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", "LoginFailReadyBinding");
        // x-message-ttl  声明队列的TTL
        args.put("x-message-ttl", 60000);
        return QueueBuilder.durable("LoginFailQueue").withArguments(args).build();
    }
    @Bean
    DirectExchange ForbidAndLoginExchange() {
        return new DirectExchange("ForbidAndLoginExchange");
    }
    @Bean
    Binding UserForbidBinding() {return BindingBuilder.bind(UserForbidQueue()).to(ForbidAndLoginExchange()).with("UserForbidRouting");}
    @Bean
    Binding LoginFailBinding() {return BindingBuilder.bind(LoginFailQueue()).to(ForbidAndLoginExchange()).with("LoginFailRouting");}
    //死信队列和其routing
    @Bean
    public Queue UserForbidReadyQueue() {
        return new Queue("UserForbidReadyQueue",true);
    }
    @Bean
    public Queue LoginFailReadyQueue() {
        return new Queue("LoginFailReadyQueue",true);
    }
    @Bean
    Binding UserForbidReadyBinding() {return BindingBuilder.bind(UserForbidReadyQueue()).to(ForbidAndLoginExchange()).with("UserForbidReadyRouting");}
    @Bean
    Binding LoginFailReadyBinding() {return BindingBuilder.bind(LoginFailReadyQueue()).to(ForbidAndLoginExchange()).with("LoginFailReadyRouting");}

    //延时博客publis队列
    @Bean
    public Queue BlogPublishQueue() {
        Map<String, Object> args = new HashMap<>(2);
        // x-dead-letter-exchange    这里声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", "BlogPublishExchange");
        // x-dead-letter-routing-key  这里声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", "BlogPublishReadyBinding");
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