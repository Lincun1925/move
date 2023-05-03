package com.wsh.orderservice.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 分布式锁实现一人一单
 */
@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;

    @Bean
    public RedissonClient redissonClient() {
        //配置类
        Config config = new Config();
        //添加redis节点地址
        config.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword("1234");
        //创建客户端
        return Redisson.create(config);
    }
}
