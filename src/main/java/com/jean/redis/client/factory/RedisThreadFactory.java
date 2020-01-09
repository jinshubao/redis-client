package com.jean.redis.client.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class RedisThreadFactory implements ThreadFactory {

    private static final Logger logger = LoggerFactory.getLogger(RedisThreadFactory.class);
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, "redis-task-thread-" + String.format("%02d", this.threadNumber.getAndIncrement()));
        logger.debug("create new thread[{}]", thread.getName());
        if (!thread.isDaemon()) {
            thread.setDaemon(true);
        }
        if (thread.getPriority() != 5) {
            thread.setPriority(5);
        }
        return thread;
    }
}
