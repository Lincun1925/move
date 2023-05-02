package com.wsh.orderservice.entity;


import com.wsh.feignapi.entity.Car;
import java.time.LocalDateTime;

public class OrderDetail extends Orders{
    private String start;
    private String middle;
    private String end;
    private LocalDateTime startTime;

    public OrderDetail(Car car){
        this.start = car.getStart();
        this.middle = car.getMiddle();
        this.end = car.getEnd();
        this.startTime = car.getStartTime();
    }
}
