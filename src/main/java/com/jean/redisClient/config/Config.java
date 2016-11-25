package com.jean.redisClient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by jinshubao on 2016/11/25.
 */
@Configuration
public class Config {

    @Bean
    Executor executor() {
        return Executors.newCachedThreadPool();
    }
}
