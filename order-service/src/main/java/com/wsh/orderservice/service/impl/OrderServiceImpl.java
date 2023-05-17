package com.wsh.orderservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wsh.feignapi.clients.CarClient;
import com.wsh.feignapi.common.Result;
import com.wsh.feignapi.entity.Car;
import com.wsh.orderservice.entity.Orders;
import com.wsh.feignapi.entity.User;
import com.wsh.orderservice.mapper.OrderMapper;
import com.wsh.orderservice.service.OrderService;
import com.wsh.feignapi.utils.UserHolder;
import feign.FeignException;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Resource
    private RedissonClient redissonClient;

    @Resource
    private CarClient carClient;

    @Override
//    @GlobalTransactional
    public Result delete(Long id) {
        //删除用户的车票订单
        User user = UserHolder.getUser();
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        try {
            queryWrapper.eq("user_id", user.getId()).eq("car_id", id);
            boolean remove = remove(queryWrapper);
            //若删除订单成功
            if (remove) {
                //更新车次中的剩余票数
                carClient.addCar(id);
                return Result.success(null);
            }
        } catch (FeignException e) {
            log.error("下单失败原因:{}", e.contentUTF8(), e);
            throw new RuntimeException(e.contentUTF8(), e);
        }
        return Result.error("车票不存在");
    }

    @Override
//    @GlobalTransactional
    public Result buy(Long id) {
        Car car = carClient.getById(id);
        if (car.getStock() == 0) {
            return Result.error("车票已售尽");
        }
        User res = UserHolder.getUser();

        //创建锁对象
        RLock lock = redissonClient.getLock("lock:order:" + id + ":" + res.getId());
        //尝试加锁
        boolean tryLock = lock.tryLock();
        String name = lock.getName();
        log.info("name:{}", name);
        log.info("tryLock:{}", tryLock);

        if (!tryLock) {
            //获取锁失败
            return Result.error("不允许重复下单");
        }

        try {
            //获取成功，加锁
            //一人一单
            //判断是否购买
            Integer count = query().eq("user_id", res.getId()).eq("car_id", id).count();
            //已购买
            if (count > 0) {
                return Result.error("用户已购买过该车票");
            }

            //未购买
            Orders orders = new Orders(res.getId(), id);

            //生成订单
            save(orders);
            //扣减库存
            carClient.delCar(id);

            return Result.success(orders);

        } finally {
            //释放锁
            lock.unlock();
        }

    }

    @Override
    public Result get() {
        //获取用户信息
        User user = UserHolder.getUser();
        //查询语句
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        //查询用户订单
        List<Orders> ordersList = list(queryWrapper.eq("user_id", user.getId()));
        List<Long> idList = new ArrayList<>();
        //获取用户的车票carId
        for (Orders orders : ordersList) {
            idList.add(orders.getCarId());
        }

        if (idList.isEmpty()) {
            return Result.error("您尚未购票");
        }
        //获取用户车票
        List<Car> carList = carClient.getListByIds(idList);
        return Result.success(carList);
    }
}
