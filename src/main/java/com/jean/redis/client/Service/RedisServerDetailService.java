package com.jean.redis.client.Service;

import javafx.concurrent.Task;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
public class RedisServerDetailService extends BaseService {

    public RedisServerDetailService(Executor executor) {
        super(executor);
    }

    @Override
    protected Task createTask() {
        return new RedisBaseTask(getConfig()) {
            @Override
            protected Object call() throws Exception {
                return null;
            }
        };
    }
}
