package com.jean.redis.client.model;

import com.jean.redis.client.entry.Node;
import com.jean.redis.client.entry.NodeType;

/**
 * Created by jinshubao on 2016/11/28.
 */
public class RootNode implements Node {
    private String name;

    @Override
    public NodeType getNodeType() {
        return NodeType.ROOT;
    }

    public RootNode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }


}
