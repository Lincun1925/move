package com.wsh.carservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wsh.feignapi.entity.Car;
import com.wsh.carservice.mapper.CarMapper;
import com.wsh.carservice.service.CarService;
import org.springframework.stereotype.Service;

@Service
public class CarServiceImpl extends ServiceImpl<CarMapper,Car> implements CarService {

}
