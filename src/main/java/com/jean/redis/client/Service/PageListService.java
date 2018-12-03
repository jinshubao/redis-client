package com.jean.redis.client.Service;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.entry.Node;
import com.jean.redis.client.model.*;
import javafx.scene.control.TreeItem;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author jinshubao
 * @date 2018/12/03
 */
@Service
public class PageListService extends BaseService<Page<ListItem>> {

    private Pageable pageable;

    public void restart(TreeItem<Node> treeItem, Pageable pageable) {
        assertNotRunning();
        this.pageable = pageable;
        Node value = treeItem.getValue();
        if (value instanceof DBNode) {
            this.hostName = ((HostNode) value).getHostName();
            this.port = ((HostNode) value).getPort();
            this.auth = ((HostNode) value).getAuth();
            DBNode dbNode = (DBNode) value;
            this.dbIndex = dbNode.getDbIndex();
            if (value instanceof DirNode) {
                DirNode dirModel = (DirNode) value;
                pageable.setPattern(dirModel.getFullPath() + pageable.getPattern());
            }
            super.restart();
        }
    }

    @Override
    protected Page<ListItem> task() {
        Long dbSize = jedis.dbSize();
        ScanParams params = new ScanParams();
        params.count(pageable.getCount());
        params.match(pageable.getPattern());
        ScanResult<String> scanResult = jedis.scan(pageable.getCursor(), params);
        Page<ListItem> page = new Page<>(scanResult.getStringCursor(), pageable.getCount(), dbSize, new ArrayList<>());
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
            page.getContents().add(listItem);
        });
        return page;
    }
}
