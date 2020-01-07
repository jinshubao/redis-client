package com.jean.redis.client.model;

import java.util.Collection;
import java.util.Set;

public class RedisSetValue extends RedisValue<Set<byte[]>> {

    public RedisSetValue(byte[] key, Set<byte[]> value, long ttl) {
        super(key, value, ttl);
    }

    public Collection<byte[]> toList() {
        return this.value.get();
    }
}
