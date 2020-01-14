package com.jean.redis.client.task;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.model.RedisServerProperty;
import com.jean.redis.client.model.RedisValue;
import com.jean.redis.client.model.RedisValueWrapper;
import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RedisValueTask extends BaseTask<RedisValueWrapper> {

    private final int database;

    private final byte[] key;


    public RedisValueTask(RedisServerProperty serverProperty, int database, byte[] key, EventHandler<WorkerStateEvent> eventHandler) {
        super(serverProperty, eventHandler);
        this.database = database;
        this.key = key;
    }


    @Override
    protected RedisValueWrapper call() throws Exception {
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

    private RedisValueWrapper getStringValue(RedisCommands<byte[], byte[]> commands) {
        byte[] value = commands.get(key);
        Long ttl = commands.ttl(key);
        updateProgress(1L, 1L);
        ArrayList<RedisValue> list = new ArrayList<>();
        list.add(new RedisValue(null, value));
        return new RedisValueWrapper(serverProperty.getUuid(), key, CommonConstant.KeyType.STRING, ttl, 1L, list);
    }

    private RedisValueWrapper getHashValue(RedisCommands<byte[], byte[]> commands) {
        Long size = commands.hlen(key);
        Long ttl = commands.ttl(key);
        List<RedisValue> value = new ArrayList<>();
        if (size > 0) {
            ScanArgs scanArgs = ScanArgs.Builder.limit(CommonConstant.VALUE_SCAN_SIZE);
            ScanCursor scanCursor = ScanCursor.INITIAL;
            do {
                MapScanCursor<byte[], byte[]> cursor = commands.hscan(key, scanCursor, scanArgs);
                scanCursor = ScanCursor.of(cursor.getCursor());
                scanCursor.setFinished(cursor.isFinished());
                cursor.getMap().forEach((k, v) -> {
                    value.add(new RedisValue(k, v));
                });
                updateProgress(size, value.size());
                if (isCancelled()) {
                    break;
                }
            } while (!scanCursor.isFinished());
        }
        return new RedisValueWrapper(serverProperty.getUuid(), key, CommonConstant.KeyType.HASH, ttl, size, value);
    }


    private RedisValueWrapper getListValue(RedisCommands<byte[], byte[]> commands) {
        Long size = commands.llen(key);
        Long ttl = commands.ttl(key);
        long scanSize = CommonConstant.VALUE_SCAN_SIZE;
        if (size <= scanSize) {
            List<RedisValue> value = commands.lrange(key, 0, -1).stream().map(item -> new RedisValue(null, item)).collect(Collectors.toList());
            return new RedisValueWrapper(serverProperty.getUuid(), key, CommonConstant.KeyType.LIST, ttl, size, value);
        }
        List<RedisValue> value = new ArrayList<>();
        long start = 0;
        long stop = start + scanSize - 1;
        List<byte[]> list;
        while (!(list = commands.lrange(key, start, stop)).isEmpty()) {
            List<RedisValue> ls = list.stream().map(item -> new RedisValue(null, item)).collect(Collectors.toList());
            value.addAll(ls);
            updateProgress(value.size(), size);
            if (isCancelled()) {
                break;
            }
        }
        return new RedisValueWrapper(serverProperty.getUuid(), key, CommonConstant.KeyType.LIST, ttl, size, value);
    }


    @SuppressWarnings("Duplicates")
    private RedisValueWrapper getScoredSetValue(RedisCommands<byte[], byte[]> commands) {
        Long size = commands.llen(key);
        Long ttl = commands.ttl(key);
        List<RedisValue> value = new ArrayList<>();
        if (size > 0) {
            ScanArgs scanArgs = ScanArgs.Builder.limit(CommonConstant.VALUE_SCAN_SIZE);
            ScanCursor scanCursor = ScanCursor.INITIAL;
            do {
                ScoredValueScanCursor<byte[]> cursor = commands.zscan(key, scanCursor, scanArgs);
                scanCursor = ScanCursor.of(cursor.getCursor());
                scanCursor.setFinished(cursor.isFinished());
                List<RedisValue> ls = cursor.getValues().stream().map(item -> new RedisValue(null, item.getValue(), item.getScore())).collect(Collectors.toList());
                value.addAll(ls);
                updateProgress(value.size(), size);
                if (isCancelled()) {
                    break;
                }
            } while (!scanCursor.isFinished());
        }
        return new RedisValueWrapper(serverProperty.getUuid(), key, CommonConstant.KeyType.ZSET, ttl, size, value);
    }

    @SuppressWarnings("Duplicates")
    private RedisValueWrapper getSetValue(RedisCommands<byte[], byte[]> commands) {
        Long size = commands.scard(key);
        Long ttl = commands.ttl(key);
        List<RedisValue> value = new ArrayList<>();
        if (size > 0) {
            ScanArgs scanArgs = ScanArgs.Builder.limit(CommonConstant.VALUE_SCAN_SIZE);
            ScanCursor scanCursor = ScanCursor.INITIAL;
            do {
                ValueScanCursor<byte[]> cursor = commands.sscan(key, scanCursor, scanArgs);
                scanCursor = ScanCursor.of(cursor.getCursor());
                scanCursor.setFinished(cursor.isFinished());
                List<RedisValue> ls = cursor.getValues().stream().map(item -> new RedisValue(null, item)).collect(Collectors.toList());
                value.addAll(ls);
                updateProgress(value.size(), size);
                if (isCancelled()) {
                    break;
                }
            } while (!scanCursor.isFinished());
        }
        return new RedisValueWrapper(serverProperty.getUuid(), key, CommonConstant.KeyType.SET, ttl, size, value);
    }


    @Override
    public String toString() {
        return "getValue-task-" + super.toString();
    }
}
