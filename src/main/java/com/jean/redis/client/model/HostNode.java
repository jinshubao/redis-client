package com.jean.redis.client.model;

import com.jean.redis.client.entry.Node;
import com.jean.redis.client.entry.NodeType;

public class HostNode implements Node {

    private ConfigProperty config;


    public HostNode(ConfigProperty config) {
        this.config = config;
    }

    public ConfigProperty getConfig() {
        return config;
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.HOST;
    }

    @Override
    public String toString() {
        return config.getHost() + ":" + config.getPort();
    }
}
