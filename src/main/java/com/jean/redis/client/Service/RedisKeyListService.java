package com.jean.redis.client.Service;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.model.ConfigProperty;
import com.jean.redis.client.model.RedisKey;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import javafx.concurrent.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
@Service
public class RedisKeyListService extends BaseService<List<RedisKey>> {

    private String pattern;
    private Long count;


    public RedisKeyListService(Executor executor) {
        super(executor);
    }

    @Override
    public void restart() {
        if (!isRunning()) {
            super.restart();
        }
    }

    public void restart(ConfigProperty config, String pattern, Long count) {
        if (!isRunning()) {
            this.config = config;
            this.pattern = pattern;
            this.count = count;
            this.restart();
        }
    }

    @Override
    protected Task<List<RedisKey>> createTask() {
        return new RedisBaseTask<List<RedisKey>>(this.getConfig()) {
            @Override
            protected List<RedisKey> call() throws Exception {
                try (StatefulRedisConnection<byte[], byte[]> connection = getRedisConnection()) {
                    RedisCommands<byte[], byte[]> redisCommands = connection.sync();
                    KeyScanCursor<byte[]> scanCursor = null;
                    List<RedisKey> result = new ArrayList<>();
                    ScanArgs limit = ScanArgs.Builder.limit(count).match(pattern);
                    do {
                        if (scanCursor == null) {
                            scanCursor = redisCommands.scan(limit);
                        } else {
                            scanCursor = redisCommands.scan(scanCursor, limit);
                        }
                        List<byte[]> keys = scanCursor.getKeys();
                        List<RedisKey> collect = keys.stream().map(key -> {
                            Long ttl = redisCommands.ttl(key);
                            String type = redisCommands.type(key);
                            RedisKey redisKey = new RedisKey();
                            redisKey.setConfig(this.config);
                            redisKey.setKey(key);
                            redisKey.setType(type);
                            redisKey.setTtl(ttl);
                            if (CommonConstant.KeyType.NONE.equals(type)) {
                                redisKey.setSize(0L);
                            } else if (CommonConstant.KeyType.STRING.equals(type)) {
                                redisKey.setSize(1L);
                            } else if (CommonConstant.KeyType.LIST.equals(type)) {
                                redisKey.setSize(redisCommands.llen(key));
                            } else if (CommonConstant.KeyType.SET.equals(type)) {
                                redisKey.setSize(redisCommands.scard(key));
                            } else if (CommonConstant.KeyType.ZSET.equals(type)) {
                                redisKey.setSize(redisCommands.zcard(key));
                            } else if (CommonConstant.KeyType.HASH.equals(type)) {
                                redisKey.setSize(redisCommands.hlen(key));
                            }
                            return redisKey;
                        }).collect(Collectors.toList());

                        result.addAll(collect);
                    } while (!scanCursor.isFinished());
                    return result;
                } catch (Throwable e) {
                    throw new Exception(e);
                }
            }
        };
    }
}
