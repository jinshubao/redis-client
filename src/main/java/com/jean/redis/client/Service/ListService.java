package com.jean.redis.client.Service;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.entry.Node;
import com.jean.redis.client.model.DBNode;
import com.jean.redis.client.model.DirNode;
import com.jean.redis.client.model.HostNode;
import com.jean.redis.client.model.ListItem;
import javafx.scene.control.TreeItem;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
@Service
public class ListService extends BaseService<List<ListItem>> {

    private TreeItem<Node> treeItem;

    public void restart(TreeItem<Node> treeItem) {
        this.treeItem = treeItem;
        Node value = treeItem.getValue();
        params.put("cmd", "*");
        if (value instanceof DBNode) {
            this.hostName = ((HostNode) value).getHostName();
            this.port = ((HostNode) value).getPort();
            this.auth = ((HostNode) value).getAuth();
            DBNode dbNode = (DBNode) value;
            this.dbIndex = dbNode.getDbIndex();
            if (value instanceof DirNode) {
                DirNode dirModel = (DirNode) value;
                params.put("cmd", dirModel.getFullPath() + "*");
            }
            super.restart();
        }
    }

    @Override
    protected List<ListItem> task() {
        String cmd = (String) params.get("cmd");
        List<ListItem> result = new ArrayList<>();
        if (cmd != null) {
            String cursor = "0";
            ScanParams params = new ScanParams();
            params.match(cmd);
            params.count(CommonConstant.SCAN_MAX_COUNT);
            for (; ; ) {
                ScanResult<String> scanResult = jedis.scan(cursor, params);
                List<String> keys = scanResult.getResult();
                keys.stream().filter(Objects::nonNull).forEach(key -> {
                    String dataType = jedis.type(key);
                    ListItem listItem = new ListItem(hostName, port, auth, dbIndex);
                    listItem.setKey(key);
                    listItem.setType(dataType);
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
                    listItem.setSize(size);
                    result.add(listItem);
                });

                if ("0".equals(scanResult.getStringCursor())) {
                    break;
                } else {
                    cursor = scanResult.getStringCursor();
                }
            }
        }
        return result;
    }


    public TreeItem<Node> getTreeItem() {
        return treeItem;
    }
}
