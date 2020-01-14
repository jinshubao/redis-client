package com.jean.redis.client.constant;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.apache.commons.pool2.ObjectPool;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
public class CommonConstant {

    public static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;

    public static final int THREAD_POOL_SIZE = 10;

    public static final long KEY_SCAN_SIZE = 100;

    public static final long VALUE_SCAN_SIZE = 100;

    public static final Map<String, RedisClient> GLOBAL_REDIS_CLIENT_CACHE = new ConcurrentHashMap<>();

    public static final Map<String, ObjectPool<StatefulRedisConnection<byte[], byte[]>>> GLOBAL_REDIS_CONNECTION_POOL_CACHE = new HashMap<>();

    public static void putConnectionPool(String uuid, ObjectPool<StatefulRedisConnection<byte[], byte[]>> value) {
        GLOBAL_REDIS_CONNECTION_POOL_CACHE.put(uuid, value);
    }

    public static ObjectPool<StatefulRedisConnection<byte[], byte[]>> removeConnectionPool(String uuid) {
        return GLOBAL_REDIS_CONNECTION_POOL_CACHE.remove(uuid);
    }

    public static ObjectPool<StatefulRedisConnection<byte[], byte[]>> getConnectionPool(String uuid) {
        return GLOBAL_REDIS_CONNECTION_POOL_CACHE.get(uuid);
    }


    public static class KeyType {

        /**
         * key不存在
         */
        public static final String NONE = "none";

        /**
         * 字符串
         */
        public static final String STRING = "string";

        /**
         * 列表
         */
        public static final String LIST = "list";

        /**
         * 集合
         */
        public static final String SET = "set";

        /**
         * 有序集
         */
        public static final String ZSET = "zset";

        /**
         * 哈希表
         */
        public static final String HASH = "hash";

    }


}
