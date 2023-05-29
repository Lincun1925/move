package com.wsh.userservice;

import com.wsh.feignapi.clients.CarClient;
import com.wsh.feignapi.config.FeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.wsh.feignapi","com.wsh.userservice"})
@MapperScan("com.wsh.userservice.mapper")
@EnableFeignClients(clients = CarClient.class, defaultConfiguration = FeignConfig.class)
@EnableScheduling
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
