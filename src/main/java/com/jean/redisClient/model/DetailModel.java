package com.jean.redisClient.model;

import java.util.Collection;

/**
 * Created by jinshubao on 2016/11/25.
 */
public class DetailModel extends ListModel {

    public DetailModel() {
    }

    private Long ttl;

    public DetailModel(ListModel listModel) {
        this.setKey(listModel.getKey());
        this.setType(listModel.getType());
        this.setSize(listModel.getSize());
    }

    private Collection<String> values;

    public Collection<String> getValues() {
        return values;
    }

    public void setValues(Collection<String> values) {
        this.values = values;
    }

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }
}
