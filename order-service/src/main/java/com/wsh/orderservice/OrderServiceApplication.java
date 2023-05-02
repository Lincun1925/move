package com.wsh.orderservice;

import com.wsh.feignapi.clients.CarClient;
import com.wsh.feignapi.config.FeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.wsh.feignapi","com.wsh.orderservice"})
@MapperScan("com.wsh.orderservice.mapper")
@EnableFeignClients(clients = CarClient.class,defaultConfiguration = FeignConfig.class)
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
