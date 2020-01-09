package com.jean.redis.client.task;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.model.*;
import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.*;

public class RedisValueTask extends BaseTask<RedisValue<?>> {

    private static final long VALUE_SCAN_SIZE = 100;

    private final int database;

    private final byte[] key;

    public RedisValueTask(RedisServerProperty serverProperty, int database, byte[] key) {
        super(serverProperty);
        this.database = database;
        this.key = key;
    }


    @Override
    protected RedisValue call() throws Exception {
        try (StatefulRedisConnection<byte[], byte[]> connection = getConnection()) {
            RedisCommands<byte[], byte[]> commands = connection.sync();
            commands.select(database);
            String type = commands.type(key);
            if (CommonConstant.KeyType.NONE.equalsIgnoreCase(type)) {
                return null;
            }
            if (CommonConstant.KeyType.STRING.equalsIgnoreCase(type)) {
                return this.getStringValue(commands);
            }
            if (CommonConstant.KeyType.LIST.equalsIgnoreCase(type)) {
                return this.getListValue(commands);
            }
            if (CommonConstant.KeyType.SET.equalsIgnoreCase(type)) {
                return this.getSetValue(commands);
            }
            if (CommonConstant.KeyType.ZSET.equalsIgnoreCase(type)) {
                return this.getScoredSetValue(commands);
            }
            if (CommonConstant.KeyType.HASH.equalsIgnoreCase(type)) {
                return this.getHashValue(commands);
            }
            logger.debug("类型[{}]未实现", type);
            return null;
        }
    }

    private RedisStringValue getStringValue(RedisCommands<byte[], byte[]> commands) {
        byte[] value = commands.get(key);
        Long ttl = commands.ttl(key);
        updateProgress(1L, 1L);
        return new RedisStringValue(key, CommonConstant.KeyType.STRING, ttl, 1L, value);
    }

    private RedisValue<Map<byte[], byte[]>> getHashValue(RedisCommands<byte[], byte[]> commands) {
        Long size = commands.hlen(key);
        Long ttl = commands.ttl(key);
        Map<byte[], byte[]> value = new HashMap<>();
        if (size > 0) {
            ScanArgs scanArgs = ScanArgs.Builder.limit(VALUE_SCAN_SIZE);
            ScanCursor scanCursor = ScanCursor.INITIAL;
            do {
                MapScanCursor<byte[], byte[]> cursor = commands.hscan(key, scanCursor, scanArgs);
                scanCursor = ScanCursor.of(cursor.getCursor());
                scanCursor.setFinished(cursor.isFinished());
                value.putAll(cursor.getMap());
                updateProgress(size, value.size());
                if (isCancelled()) {
                    break;
                }
            } while (!scanCursor.isFinished());
        }
        return new RedisHashValue(key, CommonConstant.KeyType.HASH, ttl, size, value);
    }


    private RedisListValue getListValue(RedisCommands<byte[], byte[]> commands) {
        Long size = commands.llen(key);
        Long ttl = commands.ttl(key);
        long scanSize = VALUE_SCAN_SIZE;
        if (size <= scanSize) {
            List<byte[]> value = commands.lrange(key, 0, -1);
            return new RedisListValue(key, CommonConstant.KeyType.LIST, ttl, size, value);
        }
        List<byte[]> value = new ArrayList<>();
        long start = 0;
        long stop = start + scanSize - 1;
        List<byte[]> list;
        while (!(list = commands.lrange(key, start, stop)).isEmpty()) {
            value.addAll(list);
            updateProgress(value.size(), size);
            if (isCancelled()) {
                break;
            }
        }
        return new RedisListValue(key, CommonConstant.KeyType.LIST, ttl, size, value);
    }


    private RedisScoredSetValue getScoredSetValue(RedisCommands<byte[], byte[]> commands) {
        Long size = commands.llen(key);
        Long ttl = commands.ttl(key);
        List<byte[]> value = new ArrayList<>();
        if (size > 0) {
            ScanArgs scanArgs = ScanArgs.Builder.limit(VALUE_SCAN_SIZE);
            ScanCursor scanCursor = ScanCursor.INITIAL;
            do {
                ScoredValueScanCursor<byte[]> cursor = commands.zscan(key, scanCursor, scanArgs);
                scanCursor = ScanCursor.of(cursor.getCursor());
                scanCursor.setFinished(cursor.isFinished());
                cursor.getValues().forEach(item -> value.add(item.getValue()));
                updateProgress(value.size(), size);
                if (isCancelled()) {
                    break;
                }
            } while (!scanCursor.isFinished());
        }
        return new RedisScoredSetValue(key, CommonConstant.KeyType.ZSET, ttl, size, value);
    }


    private RedisSetValue getSetValue(RedisCommands<byte[], byte[]> commands) {
        Long size = commands.scard(key);
        Long ttl = commands.ttl(key);
        Set<byte[]> value = new HashSet<>();
        if (size > 0) {
            ScanArgs scanArgs = ScanArgs.Builder.limit(VALUE_SCAN_SIZE);
            ScanCursor scanCursor = ScanCursor.INITIAL;
            do {
                ValueScanCursor<byte[]> cursor = commands.sscan(key, scanCursor, scanArgs);
                scanCursor = ScanCursor.of(cursor.getCursor());
                scanCursor.setFinished(cursor.isFinished());
                value.addAll(cursor.getValues());
                updateProgress(value.size(), size);
                if (isCancelled()) {
                    break;
                }
            } while (!scanCursor.isFinished());
        }
        return new RedisSetValue(key, CommonConstant.KeyType.SET, ttl, size, value);
    }

}
