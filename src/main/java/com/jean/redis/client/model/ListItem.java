package com.jean.redis.client.model;

import com.jean.redis.client.entry.NodeType;

/**
 *
 * @author jinshubao
 * @date 2016/11/25
 */
public class ListItem extends DirNode {

    protected String key;
    protected String type;
    protected Long size = 0L;

    public ListItem() {
    }

    public ListItem(String key, String type, Long size) {
        this.key = key;
        this.type = type;
        this.size = size;
    }

    public ListItem(String hostName, Integer port, String auth, Integer dbIndex) {
        super(hostName, port, auth, dbIndex);
    }

    public ListItem(String hostName, Integer port, String auth, Integer dbIndex, String key, String type, Long size) {
        super(hostName, port, auth, dbIndex);
        this.key = key;
        this.type = type;
        this.size = size;
    }

    public ListItem(String hostName, Integer port, String auth, Integer dbIndex, String dirName, String fullPath, String key, String type, Long size) {
        super(hostName, port, auth, dbIndex, dirName, fullPath);
        this.key = key;
        this.type = type;
        this.size = size;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.LIST;
    }
}
