package com.jean.redis.client.model;

import javafx.beans.property.*;

import java.util.List;

public class RedisValue {

    private StringProperty uuid = new SimpleStringProperty(this, "uuid");
    private ObjectProperty<byte[]> key = new SimpleObjectProperty<>(this, "key");
    private StringProperty type = new SimpleStringProperty(this, "type");
    private LongProperty ttl = new SimpleLongProperty(this, "ttl");
    private LongProperty size = new SimpleLongProperty(this, "size");
    private List<ValueResult> value;

    public RedisValue(String uuid, byte[] key, String type, Long ttl, Long size, List<ValueResult> value) {
        this.uuid.set(uuid);
        this.key.set(key);
        this.type.set(type);
        this.ttl.set(ttl);
        this.size.set(size);
        this.value = value;
    }

    public String getUuid() {
        return uuid.get();
    }


    public void setUuid(String uuid) {
        this.uuid.set(uuid);
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

    public List<ValueResult> getValue() {
        return value;
    }

    public void setValue(List<ValueResult> value) {
        this.value = value;
    }
}
