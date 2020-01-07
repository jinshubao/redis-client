package com.jean.redis.client.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RedisStringValue extends RedisValue<byte[]> {

    public RedisStringValue(byte[] key, byte[] value, long ttl) {
        super(key, value, ttl);
    }

    public Collection<byte[]> toList() {
        List<byte[]> list = new ArrayList<>(1);
        list.add(value.get());
        return list;
    }
}
