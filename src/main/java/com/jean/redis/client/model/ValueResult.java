package com.jean.redis.client.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ValueResult {

    private ObjectProperty<byte[]> key = new SimpleObjectProperty<>(this, "key");
    private ObjectProperty<byte[]> value = new SimpleObjectProperty<>(this, "key");

    public ValueResult(byte[] key, byte[] value) {
        this.key.set(key);
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

    public byte[] getValue() {
        return value.get();
    }

    public ObjectProperty<byte[]> valueProperty() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value.set(value);
    }
}
