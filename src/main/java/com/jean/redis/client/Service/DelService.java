package com.jean.redis.client.Service;

import com.jean.redis.client.model.ConfigProperty;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import javafx.concurrent.Task;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
@Service
public class DelService extends BaseService<Void> {

    public DelService(Executor executor) {
        super(executor);
    }

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
        return new RedisBaseTask<Void>(this.getConfig()) {
            @Override
            protected Void call() throws Exception {
                byte[][] keyBytes = new byte[keys.length][];
                for (int i = 0; i < keys.length; i++) {
                    keyBytes[i] = keys[i].getBytes();
                }
                StatefulRedisConnection<byte[], byte[]> redisConnection = getRedisConnection();
                RedisCommands<byte[], byte[]> redisCommands = redisConnection.sync();
                redisCommands.del(keyBytes);
                return null;
            }
        };
    }
}
