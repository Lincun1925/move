package com.wsh.emailservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列
 */
@Configuration
public class EmailMessageConfig {
    //失败拒绝策略，默认reject进入死信队列，可以自定义
//    @Bean
//    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate){
//        return new RepublishMessageRecoverer
//                (rabbitTemplate,"error.direct","error");
//    }
    //交换机
    @Bean
    public DirectExchange emailExchange(){
        return new DirectExchange("email.direct");
    }
    //队列
    @Bean
    public Queue emailQueue(){
        return QueueBuilder.durable("email.queue")
                .deadLetterExchange("dl.direct")
                .deadLetterRoutingKey("dl")
                .build();
    }
    //绑定
    @Bean
    public Binding emailBinding(){
        return BindingBuilder.bind(emailQueue())
                .to(emailExchange())
                .with("email");
    }
}
