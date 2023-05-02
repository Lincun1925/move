package com.wsh.orderservice.controller;



import com.wsh.feignapi.common.Result;
import com.wsh.orderservice.service.OrderService;
import com.wsh.feignapi.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 购买车票
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result buy(@PathVariable Long id){
        return orderService.buy(id);
    }

    /**
     * 查询订单
     * @return
     */
    @GetMapping
    public Result get(){
        return orderService.get();
    }

    /**
     * 删除订单
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id){
        return orderService.delete(id);
    }
}
