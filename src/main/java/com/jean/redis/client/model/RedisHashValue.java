package com.jean.redis.client.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class RedisHashValue extends RedisValue<Map<byte[], byte[]>> {

    private static final String HASH_SP = ":";

    private static final byte[] HASH_SP_BYTES = HASH_SP.getBytes();

    public RedisHashValue(byte[] key, Map<byte[], byte[]> value, long ttl) {
        super(key, value, ttl);
    }

    public Collection<byte[]> toList() {
        Map<byte[], byte[]> hash = value.get();
        List<byte[]> list = new ArrayList<>(hash.size());
        hash.forEach((key, value) -> {
            ByteBuffer buffer = ByteBuffer.allocate(key.length + value.length + HASH_SP_BYTES.length);
            buffer.put(key);
            buffer.put(HASH_SP_BYTES);
            buffer.put(value);
            list.add(buffer.array());
        });
        return list;
    }
}
