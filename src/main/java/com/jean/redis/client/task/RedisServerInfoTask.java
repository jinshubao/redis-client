package com.jean.redis.client.task;

import com.jean.redis.client.model.RedisServerProperty;
import io.lettuce.core.api.StatefulRedisConnection;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class RedisServerInfoTask extends BaseTask<String> {


    public RedisServerInfoTask(RedisServerProperty serverProperty, EventHandler<WorkerStateEvent> eventHandler) {
        super(serverProperty, eventHandler);
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
