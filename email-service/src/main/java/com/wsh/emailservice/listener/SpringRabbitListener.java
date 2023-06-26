package com.wsh.emailservice.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wsh.emailservice.entity.MsgEntity;
import com.wsh.emailservice.mapper.MsgMapper;
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
    private MsgMapper mapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "email.queue", durable = "true"),
            exchange = @Exchange(value = "email.direct", durable = "true", autoDelete = "false"),
            key = {"email"}
    ))
    public void listen(Message message) {
        String code = ValidateCodeUtils.generateValidateCode4String(4).toString();
        //拿到消息属性
        MessageProperties properties = message.getMessageProperties();
        //获取关联的ID
        String id = properties.getCorrelationId();
        //Redis当中不存在
        if (stringRedisTemplate.opsForValue().get(id) == null) {
            QueryWrapper<MsgEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("msg_id", id);
            //并且db当中不存在，证明该消息未被消费
            MsgEntity entity = mapper.selectOne(queryWrapper);
            if (entity == null) {
                String email = new String(message.getBody());
                MailCodeUtils.sendMail(email, code);

                //1.将生成的验证码保存到sessione
//            session.setAttribute(email, code);

                //2.将生成的验证码保存到redis，有效时间1分钟，不存在则创建
                stringRedisTemplate.opsForValue().setIfAbsent("login:code:" + email, code, 1, TimeUnit.MINUTES);
                //消息成功消费，把消息id存入db
                MsgEntity msgEntity = new MsgEntity(Long.valueOf(id));
                mapper.insert(msgEntity);
                //把id存入redis，防止重复消费，缓存有效期为1天
                stringRedisTemplate.opsForValue().set(id, "success", 1, TimeUnit.DAYS);
            } else {
                //db存在，redis不存在，证明缓存过期，则更新缓存
                stringRedisTemplate.opsForValue().set(id, "success", 1, TimeUnit.DAYS);
            }

        }

    }
}
