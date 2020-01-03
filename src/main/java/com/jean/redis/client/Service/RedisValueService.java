package com.jean.redis.client.Service;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.model.ConfigProperty;
import com.jean.redis.client.model.RedisValue;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import javafx.concurrent.Task;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
@Service
public class RedisValueService extends BaseService<RedisValue> {

    private byte[] key;

    public RedisValueService(Executor executor) {
        super(executor);
    }

    @Override
    public void restart() {
        if (!isRunning()) {
            super.restart();
        }
    }

    public void restart(ConfigProperty config, byte[] key) {
        if (!isRunning()) {
            this.config = config;
            this.key = key;
            this.restart();
        }
    }

    @Override
    protected Task<RedisValue> createTask() {
        return new RedisBaseTask<RedisValue>(this.getConfig()) {
            @Override
            protected RedisValue call() throws Exception {
                byte[] keyBytes = key;
                try (StatefulRedisConnection<byte[], byte[]> connection = getRedisConnection()) {
                    RedisCommands<byte[], byte[]> redisCommands = connection.sync();
                    String type = redisCommands.type(keyBytes);
                    Long ttl = redisCommands.ttl(keyBytes);
                    if (CommonConstant.KeyType.STRING.equals(type)) {
                        byte[] value = redisCommands.get(keyBytes);
                        return new RedisValue(keyBytes, value, type, ttl);
                    } else if (CommonConstant.KeyType.HASH.equals(type)) {
                        Map<byte[], byte[]> value = redisCommands.hgetall(keyBytes);
                        return new RedisValue(keyBytes, value, type, ttl);
                    } else if (CommonConstant.KeyType.SET.equals(type)) {
                        Set<byte[]> value = redisCommands.smembers(keyBytes);
                        return new RedisValue(keyBytes, value, type, ttl);
                    } else if (CommonConstant.KeyType.ZSET.equals(type)) {
                        Long zcard = redisCommands.zcard(keyBytes);
                        if (zcard > 0L) {
                            List<byte[]> zrange = redisCommands.zrange(keyBytes, 0L, zcard - 1);
                            return new RedisValue(keyBytes, zrange, type, ttl);
                        } else {
                            return new RedisValue(keyBytes, Collections.emptyList(), type, ttl);
                        }
                    } else if (CommonConstant.KeyType.LIST.equals(type)) {

                        Long llen = redisCommands.llen(keyBytes);
                        if (llen > 0L) {
                            List<byte[]> lrange = redisCommands.lrange(keyBytes, 0L, llen - 1);
                            return new RedisValue(keyBytes, lrange, type, ttl);
                        } else {
                            return new RedisValue(keyBytes, Collections.emptyList(), type, ttl);
                        }
                    }
                    return new RedisValue(keyBytes, null, type, ttl);
                } catch (Throwable e) {
                    throw new Exception(e);
                }
            }
        };
    }
}
