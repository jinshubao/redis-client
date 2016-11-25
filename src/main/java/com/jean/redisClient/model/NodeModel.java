package com.jean.redisClient.model;

import java.util.List;

/**
 * Created by jinshubao on 2016/11/25.
 */
public class NodeModel extends BaseModel {

    private String nodeName;
    private NodeModel nodeModel;
    private List<BaseModel> models;

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public NodeModel getNodeModel() {
        return nodeModel;
    }

    public void setNodeModel(NodeModel nodeModel) {
        this.nodeModel = nodeModel;
    }

    public List<BaseModel> getModels() {
        return models;
    }

    public void setModels(List<BaseModel> models) {
        this.models = models;
    }
}
