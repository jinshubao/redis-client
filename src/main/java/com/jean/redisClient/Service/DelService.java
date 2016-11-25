package com.jean.redisClient.Service;

import com.jean.redisClient.model.BaseModel;
import javafx.concurrent.Task;

/**
 * Created by jinshubao on 2016/11/25.
 */
@org.springframework.stereotype.Service
public class DelService extends BaseService<BaseModel> {

    @Override
    protected Task<BaseModel> createTask() {
        return new Task<BaseModel>() {
            @Override
            protected BaseModel call() throws Exception {
                printCurrentThreadName();
                BaseModel item = (BaseModel) params.get("item");
                if (item != null && item.getKey() != null) {
                    redisTemplate.delete(item.getKey());
                    return item;
                }
                return null;
            }
        };
    }
}
