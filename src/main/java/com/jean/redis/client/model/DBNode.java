package com.jean.redis.client.model;

import com.jean.redis.client.entry.Node;
import com.jean.redis.client.entry.NodeType;

public class DBNode extends HostNode implements Node {

    public DBNode(ConfigProperty config) {
        super(config);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.DB;
    }

    @Override
    public String toString() {
        return "db" + getConfig().getDatabase();
    }
}
