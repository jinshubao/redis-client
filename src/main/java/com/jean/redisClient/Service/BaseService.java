package com.jean.redisClient.Service;

import javafx.concurrent.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by jinshubao on 2016/11/25.
 */
public abstract class BaseService<V> extends Service {

    @Autowired
    public void executor(Executor executor) {
        this.setExecutor(executor);
    }

    @Autowired
    protected RedisTemplate<String, String> redisTemplate;

    protected Map<String, Object> params = new HashMap<>();

    public void setParams(Map<String, Object> params) {
        this.params.putAll(params);
    }

    public void addParams(String key, Object value) {
        this.params.put(key, value);
    }

    public void clearParams() {
        if (!isRunning()) {
            params.clear();
        }
    }

    protected void printCurrentThreadName() {
        System.out.println(Thread.currentThread().getName());
    }

    @Override
    public void restart() {
        if (!isRunning()) {
            super.restart();
        }
    }
}
