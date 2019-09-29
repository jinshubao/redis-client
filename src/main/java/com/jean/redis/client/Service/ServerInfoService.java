package com.jean.redis.client.Service;

import javafx.concurrent.Task;

public class ServerInfoService extends BaseService {

    @Override
    protected Task createTask() {
        return new RedisTask(getConfig()) {
            @Override
            protected Object call() throws Exception {
                return null;
            }
        };
    }
}
