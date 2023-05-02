package com.wsh.feignapi.utils;


import com.wsh.feignapi.entity.User;
import com.wsh.feignapi.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户拦截
 */
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        //判断tl中是否有user
        User user = UserHolder.getUser();
        if (user == null) {
            //无用户则拦截
            response.setStatus(401);
            return false;
        }
        if (user.getStatus() == 1) {
            //用户无管理员权限则拦截
            response.setStatus(401);
            return false;
        }
        //有管理员用户，放行
        log.info("user:{}",user);
        return true;
    }
}
