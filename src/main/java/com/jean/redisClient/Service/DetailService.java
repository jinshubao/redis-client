package com.jean.redisClient.Service;

import com.jean.redisClient.constant.CommonConstant;
import com.jean.redisClient.model.BaseModel;
import com.jean.redisClient.model.DetailModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by jinshubao on 2016/11/25.
 */
@Service
public class DetailService extends BaseService<DetailModel> {

    @Override
    public DetailModel task() {
        BaseModel model = (BaseModel) params.get("item");
        if (model == null || model.getKey() == null) {
            return null;
        }
        DetailModel detail = new DetailModel(model);
        Collection<String> collection = new ArrayList<>();
        if (CommonConstant.REDIS_TYPE_STRING.equalsIgnoreCase(model.getType())) {
            String value = jedis.get(model.getKey());
            collection.add(value);
        } else if (CommonConstant.REDIS_TYPE_LIST.equalsIgnoreCase(model.getType())) {
            collection.addAll(jedis.lrange(model.getKey(), 0, -1));
        } else if (CommonConstant.REDIS_TYPE_SET.equalsIgnoreCase(model.getType())) {
            collection.addAll(jedis.smembers(model.getKey()));
        } else if (CommonConstant.REDIS_TYPE_ZSET.equalsIgnoreCase(model.getType())) {
            collection.addAll(jedis.zrange(model.getKey(), 0, -1));
        } else if (CommonConstant.REDIS_TYPE_HASH.equalsIgnoreCase(model.getType())) {
            Map<String, String> map = jedis.hgetAll(model.getKey());
            map.forEach((key, value) -> collection.add(key + ":" + value));
        }
        detail.setTtl(jedis.ttl(model.getKey()));
        detail.setSize((long) collection.size());
        detail.setValues(collection);
        return detail;
    }
}
