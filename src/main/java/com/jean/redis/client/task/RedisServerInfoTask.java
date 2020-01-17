package com.jean.redis.client.task;

import com.jean.redis.client.model.RedisServerProperty;
import io.lettuce.core.api.StatefulRedisConnection;

public class RedisServerInfoTask extends BaseTask<String> {


    public RedisServerInfoTask(RedisServerProperty serverProperty) {
        super(serverProperty);
        updateMessage("服务器信息");
    }

    @Override
    protected String call() throws Exception {
        try (StatefulRedisConnection<byte[], byte[]> connection = getConnection()) {
            return connection.sync().info();
        }
    }

    @Override
    public String toString() {
        return "server-info-task-" + super.toString();
    }
}
