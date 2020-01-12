package com.jean.redis.client.task;

import com.jean.redis.client.model.RedisKey;
import com.jean.redis.client.model.RedisServerProperty;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RedisKeysTask extends BaseTask<List<RedisKey>> {

    private static final long KEY_SCAN_SIZE = 100;

    private final int database;

    private final Charset charset;

    public RedisKeysTask(RedisServerProperty serverProperty, int database, Charset charset) {
        super(serverProperty);
        this.database = database;
        this.charset = charset;
    }

    @Override
    protected List<RedisKey> call() throws Exception {
        try (StatefulRedisConnection<byte[], byte[]> connection = getConnection()) {
            RedisCommands<byte[], byte[]> commands = connection.sync();
            commands.select(database);
            Long size = commands.dbsize();
            ScanCursor scanCursor = ScanCursor.INITIAL;
            List<RedisKey> value = new ArrayList<>();
            ScanArgs limit = ScanArgs.Builder.limit(KEY_SCAN_SIZE);
            do {
                KeyScanCursor<byte[]> cursor = commands.scan(scanCursor, limit);
                scanCursor = ScanCursor.of(cursor.getCursor());
                scanCursor.setFinished(cursor.isFinished());
                List<byte[]> keys = cursor.getKeys();
                List<RedisKey> collect = keys.stream().map(key -> {
                    RedisKey redisKey = new RedisKey();
                    redisKey.setServer(serverProperty);
                    redisKey.setDatabase(database);
                    redisKey.setKey(key);
//                    Long ttl = commands.ttl(key);
//                    redisKey.setTtl(ttl);
//                    String type = commands.type(key);
//                    redisKey.setType(type);
                    return redisKey;
                }).collect(Collectors.toList());
                value.addAll(collect);
                updateProgress(value.size(), size);
                if (isCancelled()) {
                    break;
                }
            } while (!scanCursor.isFinished());
            value.sort(Comparator.comparing(o -> new String(o.getKey(), charset)));
            return value;
        }
    }
}
