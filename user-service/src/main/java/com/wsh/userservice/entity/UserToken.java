package com.wsh.userservice.entity;

import com.wsh.feignapi.entity.User;
import lombok.Data;

@Data
public class UserToken extends User {
    private String token;
    public UserToken(User user,String token){
        this.token = token;
        this.setId(user.getId());
        this.setUsername(user.getUsername());
        this.setPassword(user.getPassword());
        this.setAge(user.getAge());
        this.setSex(user.getSex());
        this.setStatus(user.getStatus());
        this.setNickName(user.getNickName());
        this.setEmail(user.getEmail());
        this.setAddress(user.getAddress());
        this.setCreateTime(user.getCreateTime());
        this.setUpdateTime(user.getUpdateTime());
    }
}
