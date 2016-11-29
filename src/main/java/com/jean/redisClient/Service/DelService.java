package com.jean.redisClient.Service;

import com.jean.redisClient.model.ListModel;
import org.springframework.stereotype.Service;

/**
 * Created by jinshubao on 2016/11/25.
 */
@Service
public class DelService extends BaseService<ListModel> {

    @Override
    public ListModel task() {
        ListModel item = (ListModel) params.get("item");
        if (item != null && item.getKey() != null) {
            jedis.del(item.getKey());
            return item;
        }
        return null;
    }
}
