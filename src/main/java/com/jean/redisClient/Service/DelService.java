package com.jean.redisClient.Service;

import com.jean.redisClient.model.BaseModel;
import org.springframework.stereotype.Service;

/**
 * Created by jinshubao on 2016/11/25.
 */
@Service
public class DelService extends BaseService<BaseModel> {


    @Override
    public BaseModel task() {
        BaseModel item = (BaseModel) params.get("item");
        if (item != null && item.getKey() != null) {
            jedis.del(item.getKey());
            return item;
        }
        return null;
    }
}
