package com.jean.redis.client.model;

import com.jean.redis.client.constant.CommonConstant;

import java.nio.ByteBuffer;
import java.util.*;

public class RedisValue {

    private static final String HASH_SP = ":";

    private static final byte[] HASH_SP_BYTES = HASH_SP.getBytes();

    protected long ttl;

    protected String type;

    protected byte[] key;

    protected Object value;

    public RedisValue(byte[] key, Object value, String type, long ttl) {
        this.key = key;
        this.value = value;
        this.type = type;
        this.ttl = ttl;
    }


    public long getTtl() {
        return ttl;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public Collection<byte[]> toList() {
        if (CommonConstant.KeyType.STRING.equals(this.type)) {
            List<byte[]> list = new ArrayList<>(1);
            list.add((byte[]) value);
            return list;
        }
        if (CommonConstant.KeyType.SET.equals(this.type)) {
            return (Set<byte[]>) value;
        } else if (CommonConstant.KeyType.ZSET.equals(type)) {
            return (List<byte[]>) value;
        }
        if (CommonConstant.KeyType.LIST.equals(type)) {
            return (List<byte[]>) value;
        }
        if (CommonConstant.KeyType.HASH.equals(type)) {
            Map<byte[], byte[]> hash = (Map<byte[], byte[]>) value;
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
        return Collections.emptyList();
    }
}
