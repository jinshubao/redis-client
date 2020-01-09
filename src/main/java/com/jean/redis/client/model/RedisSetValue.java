package com.jean.redis.client.model;

import java.util.Collection;
import java.util.Set;

public class RedisSetValue extends RedisValue<Set<byte[]>> {

    public RedisSetValue(byte[] key, String type, Long ttl, Long size, Set<byte[]> value) {
        super(key, type, ttl, size, value);
    }

    public Collection<byte[]> toList() {
        return getValue();
    }
}
