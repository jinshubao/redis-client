package com.jean.redis.client.task;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.model.RedisKey;
import com.jean.redis.client.model.RedisServerProperty;
import com.jean.redis.client.util.StringUtils;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RedisKeysTask extends BaseTask<List<RedisKey>> {

    private final int database;

    private boolean typeAction;
    private boolean ttlAction;

    public RedisKeysTask(RedisServerProperty serverProperty, int database) {
        this(serverProperty, database, false, false);
    }

    public RedisKeysTask(RedisServerProperty serverProperty, int database,boolean typeAction, boolean ttlAction) {
        super(serverProperty);
        this.database = database;
        this.typeAction = typeAction;
        this.ttlAction = ttlAction;
        updateTitle("获取key");
    }

    @Override
    protected List<RedisKey> call() throws Exception {
        try (StatefulRedisConnection<byte[], byte[]> connection = getConnection()) {
            RedisCommands<byte[], byte[]> commands = connection.sync();
            commands.select(database);
            Long size = commands.dbsize();
            ScanCursor scanCursor = ScanCursor.INITIAL;
            List<RedisKey> value = new ArrayList<>();
            ScanArgs limit = ScanArgs.Builder.limit(CommonConstant.KEY_SCAN_SIZE);
            do {
                KeyScanCursor<byte[]> cursor = commands.scan(scanCursor, limit);
                scanCursor = ScanCursor.of(cursor.getCursor());
                scanCursor.setFinished(cursor.isFinished());
                List<byte[]> keys = cursor.getKeys();
                List<RedisKey> collect = keys.stream().map(key -> {

                    RedisKey redisKey = new RedisKey();
                    if (typeAction) {
                        String type = commands.type(key);
                        redisKey.setType(type);
                    }
                    if (ttlAction) {
                        Long ttl = commands.ttl(key);
                        redisKey.setTtl(ttl);
                    }
                    redisKey.setServer(serverProperty);
                    redisKey.setDatabase(database);
                    redisKey.setKey(key);
                    return redisKey;
                }).collect(Collectors.toList());
                value.addAll(collect);
                updateProgress(value.size(), size);
                if (isCancelled()) {
                    break;
                }
            } while (!scanCursor.isFinished());
            value.sort(Comparator.comparing(o -> StringUtils.byteArrayToString(o.getKey())));
            return value;
        }
    }

    @Override
    public String toString() {
        return "getKeys-task-" + super.toString();
    }
}
