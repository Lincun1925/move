package com.wsh.userservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wsh.feignapi.common.Result;
import com.wsh.feignapi.entity.User;
import com.wsh.userservice.entity.UserToken;
import com.wsh.userservice.mapper.UserMapper;
import com.wsh.userservice.service.UserService;
import com.wsh.feignapi.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public Result sendCode(String email) {
        if (StrUtil.isNotBlank(email)) {
            //1.异步调用验证码服务，直接存消息队列里
//            String queueName = "email.queue";
//            rabbitTemplate.convertAndSend(queueName,email);
            //2.发送给交换机，交换机路由队列
            String exchangeName = "email.direct";
            //3.correlationData设置全局唯一ID
//            rabbitTemplate.convertAndSend(exchangeName,"email",email,correlationData);
            //发送持久化消息
            Message msg = MessageBuilder.withBody(email.getBytes())//消息体转字节
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT)//消息持久化
                    .setExpiration("15000")
                    .setCorrelationId(IdUtil.getSnowflake().nextIdStr())
                    .build();
            rabbitTemplate.convertAndSend(exchangeName, "email", msg);
            return Result.success(null);
        }
        return Result.error("验证码发送失败");
    }

    @Override
    public Result register(Map map) {

        //1.从session中获取code
//        String code = map.get("emailCode").toString();


        //获取前端发送来的邮箱和emailCode
        String email = map.get("email").toString();
        String emailCode = map.get("emailCode").toString();
        //2.从redis缓存中获取code
        String code = stringRedisTemplate.opsForValue().get("login:code:" + email);

        //验证码比对
        if (code != null && code.equals(emailCode)) {
            //比对成功，先判断用户名是否已存在
            String username = map.get("username").toString();
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", username);
            User one = getOne(queryWrapper);
            if (one != null) {
                return Result.error("用户名已存在");
            }

            //若不存在，则新建
            CopyOptions copyOptions = new CopyOptions();
            User user = BeanUtil.mapToBean(map, User.class, copyOptions);

            //将用户存入mysql数据库
            save(user);
            //1.将用户存入session
//            session.setAttribute("user", user);

            //2.将用户存入redis，key为token，有效期30分钟
            //随机生成token，作为登录令牌
            String token = IdUtil.getSnowflake().nextIdStr();
//            String token = UUID.randomUUID(true).toString();
            stringRedisTemplate.opsForValue().set("token:" + token, JSON.toJSONString(user), 30, TimeUnit.MINUTES);
            //返回UserToken对象
            UserToken userToken = new UserToken(user, token);
            return Result.success(userToken);
        } else {
            return Result.error("注册失败");
        }

    }

    @Override
    public Result login(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        User res = getOne(queryWrapper);
        if (res == null) {
            return Result.error("登陆失败");
        } else {
            //1.登陆成功将user存入session
//            session.setAttribute("user", res);
            //2.登陆成功
            //将用户存入redis，key为token，有效期30分钟
            //随机生成token，作为登录令牌
            String token = IdUtil.getSnowflake().nextIdStr();
//            String token = UUID.randomUUID(true).toString();
            stringRedisTemplate.opsForValue().set("token:" + token, JSON.toJSONString(res), 30, TimeUnit.MINUTES);
            //返回UserToken对象
            UserToken userToken = new UserToken(res, token);
            return Result.success(userToken);
        }
    }

    @Override
    public Result out(String token) {
        //删除redis缓存
        stringRedisTemplate.delete("token:" + token);
        //删除线程对象
        UserHolder.removeUser();
        return Result.success(null);
    }
}