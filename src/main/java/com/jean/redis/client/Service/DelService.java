package com.jean.redis.client.Service;

import com.jean.redis.client.model.ConfigProperty;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import javafx.concurrent.Task;
import org.springframework.stereotype.Service;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
@Service
public class DelService extends BaseService<Void> {
    @Override
    public void restart() {
    }

    @Override
    public void start() {
        super.start();
    }

    private String[] keys;


    public void restart(ConfigProperty config, String... keys) {
        if (!isRunning()) {
            this.config = config;
            this.keys = keys;
            if (keys != null && keys.length > 0) {
                super.restart();
            }

        }
    }

    @Override
    protected Task<Void> createTask() {
        return new RedisTask<Void>(this.getConfig()) {
            @Override
            protected Void call() throws Exception {
                StatefulRedisConnection<String, String> redisConnection = getRedisConnection();
                RedisCommands<String, String> redisCommands = redisConnection.sync();
                redisCommands.del(keys);
                return null;
            }
        };
    }
}
