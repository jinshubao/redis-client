package com.jean.redis.client.model;

import com.jean.redis.client.entry.Node;
import com.jean.redis.client.entry.NodeType;

public class DBNode extends HostNode implements Node {

    public DBNode() {
    }

    public DBNode(String hostName, Integer port, String auth, Integer dbIndex) {
        super(hostName, port, auth);
        this.dbIndex = dbIndex;
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.DB;
    }

    @Override
    public String toString() {
        return "db" + dbIndex;
    }
}
