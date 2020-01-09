package com.jean.redis.client.model;

import javafx.beans.property.*;

import java.util.Collection;
import java.util.Collections;

public abstract class RedisValue<V> {

    private ObjectProperty<byte[]> key = new SimpleObjectProperty<>(this, "key");
    private StringProperty type = new SimpleStringProperty(this, "type");
    private LongProperty ttl = new SimpleLongProperty(this, "ttl");
    private LongProperty size = new SimpleLongProperty(this, "size");
    private ObjectProperty<V> value = new SimpleObjectProperty<>(this, " value");

    public RedisValue(byte[] key, String type, Long ttl, Long size, V value) {
        this.key.set(key);
        this.type.set(type);
        this.ttl.set(ttl);
        this.size.set(size);
        this.value.set(value);
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

    public V getValue() {
        return value.get();
    }

    public ObjectProperty<V> valueProperty() {
        return value;
    }

    public void setValue(V value) {
        this.value.set(value);
    }

    public Collection<byte[]> toList() {
        return Collections.emptyList();
    }
}
