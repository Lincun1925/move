package com.wsh.orderservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wsh.orderservice.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
