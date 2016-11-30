package com.jean.redisClient.model;

/**
 * Created by jinshubao on 2016/11/28.
 */
public class RootModel extends NodeModel {

    public RootModel(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String location() {
        return toString();
    }
}