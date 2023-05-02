package com.wsh.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wsh.feignapi.common.Result;
import com.wsh.feignapi.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {
    public Result sendCode(String email);
    public Result register(Map map);
    public Result login(User user);
    public Result out(String token);
}
