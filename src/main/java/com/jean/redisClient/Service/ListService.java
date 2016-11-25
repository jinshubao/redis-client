package com.jean.redisClient.Service;

import com.jean.redisClient.model.BaseModel;
import com.jean.redisClient.model.ListModel;
import javafx.concurrent.Task;
import org.springframework.data.redis.connection.DataType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by jinshubao on 2016/11/25.
 */
@Service
public class ListService extends BaseService<List<BaseModel>> {

    @Override
    protected Task<List<BaseModel>> createTask() {
        return new Task<List<BaseModel>>() {
            @Override
            protected List<BaseModel> call() throws Exception {
                printCurrentThreadName();
                String cmd = (String) params.get("cmd");
                List<BaseModel> result = new ArrayList<>();
                if (cmd != null) {
                    Set<String> keys = redisTemplate.keys(cmd);
                    keys.stream().filter(key -> key != null).forEach(key -> {
                        DataType dataType = redisTemplate.type(key);
                        ListModel listModel = new ListModel();
                        listModel.setKey(key);
                        listModel.setType(dataType.code());
                        listModel.setSize(0);
                        result.add(listModel);
                    });
                }
                return result;
            }
        };
    }
}
