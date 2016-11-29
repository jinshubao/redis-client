package com.jean.redisClient.Service;

import com.jean.redisClient.constant.CommonConstant;
import com.jean.redisClient.model.ListModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by jinshubao on 2016/11/25.
 */
@Service
public class ListService extends BaseService<List<ListModel>> {

    @Override
    public List<ListModel> task() {
        String cmd = (String) params.get("cmd");
        List<ListModel> result = new ArrayList<>();
        if (cmd != null) {
            Set<String> keys = jedis.keys(cmd);
            keys.stream().filter(key -> key != null).forEach(key -> {
                String dataType = jedis.type(key);
                ListModel listModel = new ListModel();
                listModel.setKey(key);
                listModel.setType(dataType);
                Long size = 0L;
                if (CommonConstant.REDIS_TYPE_STRING.equalsIgnoreCase(dataType)) {
                    size = 1L;
                } else if (CommonConstant.REDIS_TYPE_LIST.equalsIgnoreCase(dataType)) {
                    size = jedis.llen(key);
                } else if (CommonConstant.REDIS_TYPE_SET.equalsIgnoreCase(dataType)) {
                    size = jedis.scard(key);
                } else if (CommonConstant.REDIS_TYPE_ZSET.equalsIgnoreCase(dataType)) {
                    size = jedis.zcard(key);
                } else if (CommonConstant.REDIS_TYPE_HASH.equalsIgnoreCase(dataType)) {
                    size = jedis.hlen(key);
                }
                jedis.ttl(key);
                listModel.setSize(size);
                result.add(listModel);
            });
        }
        return result;
    }
}
