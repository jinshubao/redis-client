package com.jean.redis.client.model;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
public class ListItem {
    private ConfigProperty config;

    protected String key;
    protected String type;
    protected Long size = 0L;


    public ListItem(ConfigProperty config) {
        this.config = config;
    }

    public ListItem(ConfigProperty config, String key, String type, Long size) {
        this(config);
        this.key = key;
        this.type = type;
        this.size = size;
    }

    public String getKey() {
        return key;
    }

    public ConfigProperty getConfig() {
        return config;
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


    @Override
    public String toString() {
        return this.key;
    }
}
