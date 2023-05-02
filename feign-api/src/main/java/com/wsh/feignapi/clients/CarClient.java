package com.wsh.feignapi.clients;

import com.wsh.feignapi.entity.Car;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Feign远程调用
 */
@FeignClient(value = "carservice")
public interface CarClient {
    @GetMapping("/car/feign")
    List<Car> getList();
    @PostMapping("/car/feign")
    List<Car> getListByIds(@RequestBody List<Long> list);
    //增加库存
    @GetMapping("/car/feign/add/{id}")
    void addCar(@PathVariable Long id);
    //扣减库存
    @DeleteMapping("/car/feign/del/{id}")
    void delCar(@PathVariable Long id);
    //获取车票详情
    @GetMapping("/car/feign/{id}")
    Car getById(@PathVariable Long id);

}
