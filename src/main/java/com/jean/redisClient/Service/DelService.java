package com.jean.redisClient.Service;

import com.jean.redisClient.model.ListModel;
import org.springframework.stereotype.Service;

/**
 * Created by jinshubao on 2016/11/25.
 */
@Service
public class DelService extends BaseService<ListModel> {
    @Override
    public void restart() {
    }

    private ListModel listModel;

    public void restart(ListModel listModel) {
        this.listModel = listModel;
        this.hostName = listModel.getHostName();
        this.port = listModel.getPort();
        this.auth = listModel.getAuth();
        this.dbIndex = listModel.getDbIndex();
        super.restart();
    }

    @Override
    public ListModel task() {
        if (listModel.getKey() != null) {
            jedis.del(listModel.getKey());
            return listModel;
        }
        return null;
    }
}
