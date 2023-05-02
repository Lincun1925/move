package com.wsh.orderservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单信息
 */
@Data
@TableName("orders")
public class Orders implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long carId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    public Orders(Long userId, Long carId) {
        this.userId = userId;
        this.carId = carId;
    }

    public Orders() {

    }
}
