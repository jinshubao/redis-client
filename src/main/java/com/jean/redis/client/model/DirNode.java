package com.jean.redis.client.model;

import com.jean.redis.client.entry.Node;
import com.jean.redis.client.entry.NodeType;

public class DirNode extends DBNode implements Node {
    protected String dirName;
    protected String fullPath;

    public DirNode() {
    }

    public DirNode(String hostName, Integer port, String auth, Integer dbIndex) {
        super(hostName, port, auth, dbIndex);
    }

    public DirNode(String hostName, Integer port, String auth, Integer dbIndex, String dirName, String fullPath) {
        super(hostName, port, auth, dbIndex);
        this.dirName = dirName;
        this.fullPath = fullPath;
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.DIR;
    }

    @Override
    public String toString() {
        return dirName == null ? "" : dirName;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }
}
