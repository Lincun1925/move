package com.wsh.userservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MQConfig implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //获取对象
        RabbitTemplate bean = applicationContext.getBean(RabbitTemplate.class);
        //配置消息回执函数
        bean.setReturnCallback((msg,replyCode,replyText,exchange,routingKey)->{
            //记录日志
            log.error("消息发送到队列失败，响应码:{}，失败原因:{}，交换机:{}，路由key:{}，消息:{}",replyCode,replyText,exchange,routingKey,msg.toString());
            //可再次发送消息
        });
    }
}
