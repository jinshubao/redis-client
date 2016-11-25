package com.jean.redisClient.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by jinshubao on 2016/11/25.
 */
public class DetailModel extends BaseModel {

    public DetailModel() {
    }

    public DetailModel(BaseModel baseModel) {
        this.setKey(baseModel.getKey());
        this.setType(baseModel.getType());
        this.setSize(baseModel.getSize());
    }

    private StringProperty value = new SimpleStringProperty();

    public void setValue(String value) {
        this.value.set(value);
    }

    public String getValue() {
        return value.get();
    }

    public StringProperty valueProperty() {
        return value;
    }
}
