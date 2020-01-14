package com.jean.redis.client.model;

import io.lettuce.core.api.StatefulRedisConnection;
import org.apache.commons.pool2.ObjectPool;

public class RedisPoolWrapper {

    private ObjectPool<StatefulRedisConnection<byte[], byte[]>> pool;

    private int dbNum;

    public RedisPoolWrapper(ObjectPool<StatefulRedisConnection<byte[], byte[]>> pool, int dbNum) {
        this.pool = pool;
        this.dbNum = dbNum;
    }

    public ObjectPool<StatefulRedisConnection<byte[], byte[]>> getPool() {
        return pool;
    }

    public int getDbNum() {
        return dbNum;
    }
}
