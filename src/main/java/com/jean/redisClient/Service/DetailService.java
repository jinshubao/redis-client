package com.jean.redisClient.Service;

import com.jean.redisClient.model.BaseModel;
import com.jean.redisClient.model.DetailModel;
import javafx.concurrent.Task;

/**
 * Created by jinshubao on 2016/11/25.
 */
@org.springframework.stereotype.Service
public class DetailService extends BaseService<DetailModel> {

    @Override
    protected Task<DetailModel> createTask() {
        return new Task<DetailModel>() {
            @Override
            protected DetailModel call() throws Exception {
                printCurrentThreadName();
                BaseModel model = (BaseModel) params.get("model");
                if (model == null || model.getKey() == null) {
                    return null;
                }
                String value = redisTemplate.boundValueOps(model.getKey()).get();
                DetailModel detail = new DetailModel(model);
                detail.setValue(value);
                return detail;
            }
        };
    }

}
