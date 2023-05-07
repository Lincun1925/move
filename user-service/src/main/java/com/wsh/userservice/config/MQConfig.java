package com.wsh.userservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@Configuration
public class MQConfig {
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setPort(port);
        connectionFactory.setVirtualHost(virtualHost);
        return connectionFactory;
    }
    //rabbitmq添加回调函数，成功到达队列，返回ack，无法到达队列，返回nack
    //rabbitmq添加回执函数，当mandatory设置为true，消息无法路由到队列，则执行回执，否则直接丢弃
    @Bean
    public RabbitTemplate.ConfirmCallback confirmCallback() {
        return (correlationData, b, s) -> {
            if (b) {
                log.info("消息成功发送到队列，id为{}", correlationData.getId());
            } else {
                log.error("消息发送失败，id为{}，错误信息：{}", correlationData.getId(), s);
            }
        };
    }

    @Bean
    public RabbitTemplate.ReturnCallback returnCallback() {
        return (message, code, text, exchange, routingKey) -> {
            String id = message.getMessageProperties().getCorrelationId();
            log.error("消息无法路由到队列，执行回执，id为{}", id);
        };
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setReturnCallback(returnCallback());
        rabbitTemplate.setConfirmCallback(confirmCallback());
        return rabbitTemplate;
    }
}
