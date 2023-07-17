package com.wsh.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
public class KeyResolverConfig {
    @Bean
    @Primary
    public KeyResolver apiKeyResolver() {
        //按URL限流，每秒内请求数按URL分组统计
        return exchange -> Mono.just(exchange.getRequest().getPath().toString());
    }

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getHeaders().getFirst("authorization"));
    }

    @Bean
    public KeyResolver ipKeyResolver(){
        return exchange -> Mono.just(Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getHostName());
    }
}
