package com.jean.redis.client.model;

import java.util.Collection;
import java.util.List;

public class RedisScoredSetValue extends RedisValue<List<byte[]>> {

    public RedisScoredSetValue(byte[] key, String type, Long ttl, Long size, List<byte[]> value) {
        super(key, type, ttl, size, value);
    }

    public Collection<byte[]> toList() {
        return getValue();
    }
}
