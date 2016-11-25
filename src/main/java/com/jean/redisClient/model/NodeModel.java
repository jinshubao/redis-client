package com.jean.redisClient.model;

import java.util.List;

/**
 * Created by jinshubao on 2016/11/25.
 */
public class NodeModel extends BaseModel {

    public NodeModel() {
    }

    public NodeModel(String nodeName) {
        this.nodeName = nodeName;
    }

    public NodeModel(String nodeName, List<NodeModel> nodeModels, List<BaseModel> models) {
        this.nodeName = nodeName;
        this.nodeModels = nodeModels;
        this.models = models;
    }

    private String nodeName;
    private List<NodeModel> nodeModels;
    private List<BaseModel> models;

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public List<NodeModel> getNodeModels() {
        return nodeModels;
    }

    public void setNodeModels(List<NodeModel> nodeModels) {
        this.nodeModels = nodeModels;
    }

    public List<BaseModel> getModels() {
        return models;
    }

    public void setModels(List<BaseModel> models) {
        this.models = models;
    }

    @Override
    public String toString() {
        return nodeName;
    }
}
