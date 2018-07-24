package com.jean.redis.client.model;

import com.jean.redis.client.entry.NodeType;

import java.util.Collection;

/**
 * Created by jinshubao on 2016/11/25.
 */
public class DetailItem extends ListItem {

    private Long ttl;

    private Collection<String> values;

    public DetailItem(String key, String type, Long size, Long ttl, Collection<String> values) {
        super(key, type, size);
        this.ttl = ttl;
        this.values = values;
    }

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

    @Override
    public NodeType getNodeType() {
        return NodeType.DETAIL;
    }
}
