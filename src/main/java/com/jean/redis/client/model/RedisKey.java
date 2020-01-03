package com.jean.redis.client.model;

import javafx.beans.property.*;

public class RedisKey {

    protected ObjectProperty<ConfigProperty> config = new SimpleObjectProperty<>();

    protected ObjectProperty<byte[]> key = new SimpleObjectProperty<>();

    protected StringProperty type = new SimpleStringProperty();

    protected LongProperty ttl = new SimpleLongProperty();

    protected LongProperty size = new SimpleLongProperty();

    public ConfigProperty getConfig() {
        return config.get();
    }

    public ObjectProperty<ConfigProperty> configProperty() {
        return config;
    }

    public void setConfig(ConfigProperty config) {
        this.config.set(config);
    }

    public byte[] getKey() {
        return key.get();
    }

    public ObjectProperty<byte[]> keyProperty() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key.set(key);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public long getTtl() {
        return ttl.get();
    }

    public LongProperty ttlProperty() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl.set(ttl);
    }

    public long getSize() {
        return size.get();
    }

    public LongProperty sizeProperty() {
        return size;
    }

    public void setSize(long size) {
        this.size.set(size);
    }
}
