package com.jean.redis.client.factory;

import com.jean.redis.client.model.RedisServerProperty;
import com.jean.redis.client.task.RedisConnectionPoolTask;
import com.jean.redis.client.task.RedisKeysTask;
import com.jean.redis.client.task.RedisServerInfoTask;
import com.jean.redis.client.task.RedisValueTask;

public class RedisTaskFactory {


    /**
     * 创建查询value的后台任务
     *
     * @param serverProperty
     * @param database
     * @param key
     * @return
     */
    public static RedisValueTask createValueTask(RedisServerProperty serverProperty, int database, byte[] key) {
        return new RedisValueTask(serverProperty, database, key);
    }

    /**
     * 创建获取key列表的后台任务
     *
     * @param serverProperty
     * @param database
     * @return
     */
    public static RedisKeysTask createKeyTask(RedisServerProperty serverProperty, int database) {
        return new RedisKeysTask(serverProperty, database);
    }

    /**
     * 创建获取服务器信息的后台任务
     *
     * @param serverProperty
     * @return
     */
    public static RedisServerInfoTask createServerInfoTask(RedisServerProperty serverProperty) {
        return new RedisServerInfoTask(serverProperty);
    }

    public static RedisConnectionPoolTask createRedisPoolTask(RedisServerProperty serverProperty) {
        return new RedisConnectionPoolTask(serverProperty);
    }
}
