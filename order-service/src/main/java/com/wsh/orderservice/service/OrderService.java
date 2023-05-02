package com.wsh.orderservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wsh.orderservice.entity.Orders;
import com.wsh.feignapi.common.Result;

public interface OrderService extends IService<Orders> {
    public Result delete(Long id);
    public Result buy(Long id);
    public Result get();
}
