package com.wsh.emailservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 延时队列
 */
@Configuration
public class TTLMessageConfig {
    @Bean
    public DirectExchange ttlExchange(){
        return new DirectExchange("ttl.direct");
    }
    @Bean
    public Queue ttlQueue(){
        return QueueBuilder.durable("ttl.queue")
                .deadLetterRoutingKey("dl")
                .deadLetterExchange("dl.direct")
                .ttl(10000)
                .build();
    }
    @Bean
    public Binding ttlBinding(){
        return BindingBuilder.bind(ttlQueue()).to(ttlExchange()).with("ttl");
    }

}
