package com.wsh.feignapi.config;

import com.wsh.feignapi.utils.LoginInterceptor;
import com.wsh.feignapi.utils.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 登录拦截配置
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        //登录拦截器，拦截除了登录注册之外的所有请求，判断用户是否登录
        registry.addInterceptor(new LoginInterceptor(stringRedisTemplate)).excludePathPatterns(
                "/user/login",
                "/user/chat",
                "/user/sendMsg/**",
                "/user/register",
                "/user/export",
                "/car/feign"
        ).order(0);
        //权限拦截器，判断用户是否有管理员权限
        registry.addInterceptor(new UserInterceptor()).excludePathPatterns(
                "/orders/**",
                "/user/**",
                "/car/list2",
                "/car/feign/**",
                "/car/feign"
        ).order(1);
    }
}
