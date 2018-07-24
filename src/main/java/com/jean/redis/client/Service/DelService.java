package com.jean.redis.client.Service;

import com.jean.redis.client.model.HostNode;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 *
 * @author jinshubao
 * @date 2016/11/25
 */
@Service
public class DelService extends BaseService<HostNode> {
    @Override
    public void restart() {
    }

    private String keys;

    private HostNode listItem;

    private Object binding;

    public void restart(HostNode listItem, String keys) {
        this.listItem = listItem;
        this.keys = keys;
        this.hostName = listItem.getHostName();
        this.port = listItem.getPort();
        this.auth = listItem.getAuth();
        this.dbIndex = listItem.getDbIndex();
        super.restart();
    }

    public void restart(HostNode listItem, String keys, Object binding) {
        this.binding = binding;
        this.restart(listItem, keys);
    }

    @Override
    protected HostNode task() {
        if (keys != null) {
            Set<String> keys = jedis.keys(this.keys);
            int del = 0;
            for (String key : keys) {
                Long aLong = jedis.del(key);
                del += aLong;
                logger.debug("del {} {}", key, aLong == 1 ? "OK" : "FAIL");
            }
            if (del == keys.size()) {
                return listItem;
            }
            return null;
        }
        return null;
    }

    public Object getBinding() {
        return binding;
    }

    public void setBinding(Object binding) {
        this.binding = binding;
    }
}
