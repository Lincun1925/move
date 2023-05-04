package com.wsh.emailservice.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import utils.MailCodeUtils;
import utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 监听消息队列异步发送验证码
 */
@Slf4j
@Configuration
public class SpringRabbitListener {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "email.queue",durable = "true"),
            exchange = @Exchange(value = "email.direct",durable = "true",autoDelete = "false"),
            key = {"email"}
    ))
    public void listen(Message message) {
        String code = ValidateCodeUtils.generateValidateCode4String(4).toString();
        //拿到消息属性
        MessageProperties properties = message.getMessageProperties();
        //获取关联的ID
        String id = properties.getCorrelationId();
        //Redis当中不存在，证明该消息未被消费
        if (stringRedisTemplate.opsForValue().get(id) == null) {
            String email = new String(message.getBody());
            MailCodeUtils.sendMail(email, code);
            log.info("code={}", code);

            //1.将生成的验证码保存到session
//            session.setAttribute(email, code);

            //2.将生成的验证码保存到redis，有效时间1分钟，不存在则创建
            stringRedisTemplate.opsForValue().setIfAbsent("login:code:" + email, code, 1, TimeUnit.MINUTES);
            //消息成功消费，把id存入redis，防止重复消费
            stringRedisTemplate.opsForValue().set(id, "0");
        }

    }}
