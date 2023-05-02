package com.wsh.emailservice.listener;

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
    public void listen(String email){
        String code = ValidateCodeUtils.generateValidateCode4String(4).toString();
        MailCodeUtils.sendMail(email, code);
        log.info("code={}", code);

        //1.将生成的验证码保存到session
//            session.setAttribute(email, code);

        //2.将生成的验证码保存到redis，有效时间1分钟，不存在则创建
        stringRedisTemplate.opsForValue().setIfAbsent("login:code:" + email, code, 1, TimeUnit.MINUTES);

    }
}
