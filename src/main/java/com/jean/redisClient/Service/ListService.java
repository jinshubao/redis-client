package com.jean.redisClient.Service;

import com.jean.redisClient.constant.CommonConstant;
import com.jean.redisClient.model.*;
import javafx.scene.control.TreeItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by jinshubao on 2016/11/25.
 */
@Service
public class ListService extends BaseService<List<ListModel>> {

    private TreeItem<NodeModel> treeItem;

    public void restart(TreeItem<NodeModel> treeItem) {
        if (isRunning()){
            return;
        }
        this.treeItem = treeItem;
        NodeModel value = treeItem.getValue();
        params.put("cmd", "*");
        if (value instanceof HostModel) {
            this.hostName = ((HostModel) value).getHostName();
            this.port = ((HostModel) value).getPort();
            this.auth = ((HostModel) value).getAuth();
            this.dbIndex = 0;
            if (value instanceof DbModel) {
                this.dbIndex = ((DbModel) value).getDbIndex();
                if (value instanceof DirModel) {
                    DirModel dirModel = (DirModel) value;
                    params.put("cmd", dirModel.getFullPath() + "*");
                }
            }
        }
        super.restart();
    }

    @Override
    public List<ListModel> task() {
        String cmd = (String) params.get("cmd");
        List<ListModel> result = new ArrayList<>();
        if (cmd != null) {
            Set<String> keys = jedis.keys(cmd);
            keys.stream().filter(key -> key != null).forEach(key -> {
                String dataType = jedis.type(key);
                ListModel listModel = new ListModel(new DbModel(hostName, port, auth, dbIndex));
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


    public TreeItem<NodeModel> getTreeItem() {
        return treeItem;
    }
}
