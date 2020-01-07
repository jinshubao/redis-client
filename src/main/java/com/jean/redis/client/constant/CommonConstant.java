package com.jean.redis.client.constant;

import com.jean.redis.client.model.RedisDatabaseProperty;
import com.jean.redis.client.model.RedisKey;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.apache.commons.pool2.ObjectPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
public class CommonConstant {

    public static Map<RedisDatabaseProperty, List<RedisKey>> GLOBAL_CACHE = new HashMap<>();

    public static final Map<String, RedisClient> GLOBAL_REDIS_CLIENT_CACHE = new ConcurrentHashMap<>();

    public static final Map<String, ObjectPool<StatefulRedisConnection<byte[], byte[]>>> GLOBAL_REDIS_CONNECTION_POOL_CACHE = new HashMap<>();


    public static ObjectPool<StatefulRedisConnection<byte[], byte[]>> getConnectionPool(String server) {
        return GLOBAL_REDIS_CONNECTION_POOL_CACHE.get(server);
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
