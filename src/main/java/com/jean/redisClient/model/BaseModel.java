package com.jean.redisClient.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by jinshubao on 2016/11/25.
 */
public class BaseModel {

    private StringProperty key = new SimpleStringProperty();
    private StringProperty type = new SimpleStringProperty();
    private LongProperty size = new SimpleLongProperty();

    public void setKey(String key) {
        this.key.set(key);
    }

    public String getKey() {
        return key.get();
    }

    public StringProperty keyProperty() {
        return key;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setSize(long size) {
        this.size.set(size);
    }

    public long getSize() {
        return size.get();
    }

    public LongProperty sizeProperty() {
        return size;
    }

}
