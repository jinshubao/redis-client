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


}
