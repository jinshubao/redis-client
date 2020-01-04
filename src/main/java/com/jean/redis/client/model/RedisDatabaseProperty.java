package com.jean.redis.client.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class RedisDatabaseProperty {

    private IntegerProperty index = new SimpleIntegerProperty(this, "index");

    public RedisDatabaseProperty(int index) {
        this.index.set(index);
    }

    public int getIndex() {
        return index.get();
    }

    public IntegerProperty indexProperty() {
        return index;
    }

    public void setIndex(int index) {
        this.index.set(index);
    }

    @Override
    public String toString() {
        return "db" + this.getIndex();
    }
}
