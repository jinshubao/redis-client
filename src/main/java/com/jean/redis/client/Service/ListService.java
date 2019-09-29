package com.jean.redis.client.Service;

import com.jean.redis.client.model.ConfigProperty;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import javafx.concurrent.Task;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
@Service
public class ListService extends BaseService<List<String>> {

    private String pattern;
    private Long count;

    @Override
    public void restart() {
        if (!isRunning()) {
            super.restart();
        }
    }

    public void restart(ConfigProperty config, String pattern, Long count) {
        if (!isRunning()) {
            this.config = config;
            this.pattern = pattern;
            this.count = count;
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
                    KeyScanCursor<String> scanCursor = redisCommands.scan();
                    List<String> keys = scanCursor.getKeys();
                    return keys;
                }
            }
        };
    }
}
