package com.jean.redis.client.Service;

import com.jean.redis.client.model.ConfigProperty;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import javafx.concurrent.Task;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
public class RedisServerInfoService extends BaseService {

    public RedisServerInfoService(Executor executor) {
        super(executor);
    }

    public void restart(ConfigProperty config) {
        if (!isRunning()) {
            this.config = config;
            super.start();
        }
    }

    @Override
    protected Task createTask() {
        return new RedisBaseTask(getConfig()) {
            @Override
            protected Object call() throws Exception {
                StatefulRedisConnection redisConnection = getRedisConnection();
                RedisCommands redisCommands = redisConnection.sync();
                String info = redisCommands.info();
                System.out.println(info);
                return null;
            }
        };
    }
}
