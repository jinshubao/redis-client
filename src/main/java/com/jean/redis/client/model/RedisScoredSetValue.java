package com.jean.redis.client.model;

import java.util.Collection;
import java.util.List;

public class RedisScoredSetValue extends RedisValue<List<byte[]>> {

    public RedisScoredSetValue(byte[] key, List<byte[]> value, long ttl) {
        super(key, value, ttl);
    }

    public Collection<byte[]> toList() {
        return this.value.get();
    }
}
