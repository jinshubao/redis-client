package com.jean.redis.client.model;

import javafx.beans.property.*;

import java.util.Collection;
import java.util.Collections;

public abstract class RedisValue<V> {

    protected LongProperty ttl = new SimpleLongProperty(this, "ttl");

    protected ObjectProperty<byte[]> key = new SimpleObjectProperty<>(this, "key");

    protected ObjectProperty<V> value = new SimpleObjectProperty<>(this, " value");

    public RedisValue(byte[] key, V value, long ttl) {
        this.key.set(key);
        this.value.set(value);
        this.ttl.set(ttl);
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

    public byte[] getKey() {
        return key.get();
    }

    public ObjectProperty<byte[]> keyProperty() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key.set(key);
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
