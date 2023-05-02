package com.wsh.carservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wsh.feignapi.entity.Car;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CarMapper extends BaseMapper<Car> {
}
