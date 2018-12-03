package com.jean.redis.client.constant;

import redis.clients.util.Pool;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
public class CommonConstant {

    public static String REDIS_TYPE_STRING = "string";
    public static String REDIS_TYPE_LIST = "list";
    public static String REDIS_TYPE_SET = "set";
    public static String REDIS_TYPE_HASH = "hash";
    public static String REDIS_TYPE_NONE = "none";
    public static String REDIS_TYPE_ZSET = "zset";
    public static final String CONFIG_FILE_PATH = System.getProperty("user.dir") + "/config/";
    public static final String CONFIG_FILE_NAME = "config.yml";

    /**
     * scan命令返回的最大记录数
     */
    public static final Integer SCAN_MAX_COUNT = 100;


    public static Map<String, Pool> REDIS_POOL = new HashMap<>();
}
