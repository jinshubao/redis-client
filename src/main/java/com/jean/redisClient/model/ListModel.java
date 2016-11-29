package com.jean.redisClient.model;

/**
 * Created by jinshubao on 2016/11/25.
 */
public class ListModel {

    private String key;
    private String type;
    private Long size = 0L;

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
}
