package com.jean.redis.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ExecutorConfig {

    private static final int POOL_SIZE = 10;

    @Bean
    ExecutorService executor() {
        return Executors.newFixedThreadPool(POOL_SIZE, new ThreadFactory() {

            final AtomicInteger threadNumber = new AtomicInteger(1);
            final String namePrefix = "task-thread-";

            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, this.namePrefix + this.threadNumber.getAndIncrement());
                if (!thread.isDaemon()) {
                    thread.setDaemon(true);
                }
                if (thread.getPriority() != 5) {
                    thread.setPriority(5);
                }
                return thread;
            }
        });
    }
}
