package com.jean.redis.client.model;

import com.jean.redis.client.entry.Node;
import com.jean.redis.client.entry.NodeType;

public class HostNode  implements Node {
    protected String hostName;
    protected Integer port;
    protected String auth;
    protected  Integer dbIndex;

    public HostNode() {
    }

    public HostNode(String hostName, Integer port, String auth) {
        this.hostName = hostName;
        this.port = port;
        this.auth = auth;
        this.dbIndex = 0;
    }

    public HostNode(String hostName, Integer port, String auth, Integer dbIndex) {
        this.hostName = hostName;
        this.port = port;
        this.auth = auth;
        this.dbIndex = dbIndex;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public Integer getDbIndex() {
        return dbIndex;
    }

    public void setDbIndex(Integer dbIndex) {
        this.dbIndex = dbIndex;
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.HOST;
    }

    @Override
    public String toString() {
        return hostName + ":" + port;
    }
}
