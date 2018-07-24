package com.jean.redis.client.Service;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.model.DetailItem;
import com.jean.redis.client.model.ListItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
@Service
public class DetailService extends BaseService<DetailItem> {

    @Override
    public void restart() {
    }

    public void restart(ListItem listItem) {
        this.hostName = listItem.getHostName();
        this.port = listItem.getPort();
        this.auth = listItem.getAuth();
        this.dbIndex = listItem.getDbIndex();
        super.restart();
    }


    @Override
    protected DetailItem task() {
        ListItem model = (ListItem) params.get("item");
        if (model == null || model.getKey() == null) {
            return null;
        }
        Collection<String> collection = new ArrayList<>();
        if (CommonConstant.REDIS_TYPE_STRING.equalsIgnoreCase(model.getType())) {
            logger.debug("GET {}", model.getKey());
            String value = jedis.get(model.getKey());
            collection.add(value);
        } else if (CommonConstant.REDIS_TYPE_LIST.equalsIgnoreCase(model.getType())) {
            logger.debug("LRANGE {} 0 -1", model.getKey());
            collection.addAll(jedis.lrange(model.getKey(), 0, -1));
        } else if (CommonConstant.REDIS_TYPE_SET.equalsIgnoreCase(model.getType())) {
            logger.debug("SMEMBERS {}", model.getKey());
            collection.addAll(jedis.smembers(model.getKey()));
        } else if (CommonConstant.REDIS_TYPE_ZSET.equalsIgnoreCase(model.getType())) {
            logger.debug("ZRANGE {} 0 -1", model.getKey());
            collection.addAll(jedis.zrange(model.getKey(), 0, -1));
        } else if (CommonConstant.REDIS_TYPE_HASH.equalsIgnoreCase(model.getType())) {
            logger.debug("HGETALL {}", model.getKey());
            Map<String, String> map = jedis.hgetAll(model.getKey());
            map.forEach((key, value) -> collection.add(key + ":" + value));
        }
        Long ttl = jedis.ttl(model.getKey());
        return new DetailItem(model.getKey(), model.getType(), (long) collection.size(), ttl, collection);
    }
}
