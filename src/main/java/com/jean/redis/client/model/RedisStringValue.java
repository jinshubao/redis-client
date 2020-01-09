package com.jean.redis.client.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RedisStringValue extends RedisValue<byte[]> {

    public RedisStringValue(byte[] key, String type, Long ttl, Long size, byte[] value) {
        super(key, type, ttl, size, value);
    }

    public Collection<byte[]> toList() {
        List<byte[]> list = new ArrayList<>(1);
        list.add(getValue());
        return list;
    }
}
