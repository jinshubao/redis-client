package com.jean.redis.client.constant;

import io.lettuce.core.RedisClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
public class CommonConstant {


    public static Map<String, RedisClient> REDIS_CLIENT_MAP = new HashMap<>();


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
