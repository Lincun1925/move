package com.wsh.emailservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 死信队列
 */
@Configuration
public class ErrorMessageConfig {

    //死信交换机
    @Bean
    public DirectExchange dlExchange(){
        return new DirectExchange("dl.direct");
    }
    //死信队列
    @Bean
    public Queue dlQueue(){
        return new Queue("dl.queue");
    }
    //dl绑定
    @Bean
    public Binding dlBinding(){
        return BindingBuilder
                .bind(dlQueue())
                .to(dlExchange())
                .with("dl");
    }
}
