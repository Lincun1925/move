package com.wsh.emailservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wsh.emailservice.entity.MsgEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MsgMapper extends BaseMapper<MsgEntity> {
}
