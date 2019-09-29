package com.jean.redis.client.Service;

import com.jean.redis.client.model.ConfigProperty;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import javafx.concurrent.Task;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
@Service
public class DetailService extends BaseService<List<String>> {

    private String key;

    @Override
    public void restart() {
        if (!isRunning()) {
            super.restart();
        }
    }

    public void restart(ConfigProperty config, String key) {
        if (!isRunning()) {
            this.config = config;
            this.key = key;
            this.restart();
        }
    }

    @Override
    protected Task<List<String>> createTask() {

        return new RedisTask<List<String>>(this.getConfig()) {
            @Override
            protected List<String> call() throws Exception {
                try (StatefulRedisConnection<String, String> connection = getRedisConnection()) {
                    RedisCommands<String, String> redisCommands = connection.sync();
                    String value = redisCommands.get(key);
                    Long ttl = redisCommands.ttl(key);
                    String type = redisCommands.type(key);
                    return Arrays.asList("key:" + key, "ttl:" + ttl, "type:" + type, "value:" + value);
                }
            }
        };
    }

}
