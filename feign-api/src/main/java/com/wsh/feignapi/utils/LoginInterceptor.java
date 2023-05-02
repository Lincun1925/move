package com.wsh.feignapi.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.wsh.feignapi.entity.User;
import com.wsh.feignapi.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 登录拦截
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public LoginInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        //1.从session获取user对象
//        HttpSession session = request.getSession();
//        Object user = session.getAttribute("user");
        //2.基于token获取redis中的用户
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            //无token，则拦截
            response.setStatus(401);
            return false;
        } else {
            //redis缓存过期，则拦截
            String res = stringRedisTemplate.opsForValue().get("token:" + token);
            if (StrUtil.isBlank(res)){
                response.setStatus(401);
                return false;
            }
            //存在，保存用户信息到tl
            User user = JSON.parseObject(res, User.class);
            UserHolder.saveUser(user);
            //刷新token有效期
            stringRedisTemplate.expire("token:" + token,30, TimeUnit.MINUTES);
            log.info("user:{}", user);
            return true;
        }

    }
}
