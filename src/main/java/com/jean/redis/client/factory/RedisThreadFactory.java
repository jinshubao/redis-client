package com.jean.redis.client.factory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class RedisThreadFactory implements ThreadFactory {

    //默认线程栈大小为0
    private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = this.defaultFactory.newThread(runnable);
        thread.setName("redis-task-thread-" + this.threadNumber.getAndIncrement());
        if (!thread.isDaemon()) {
            thread.setDaemon(true);
        }
        return thread;
    }
}
