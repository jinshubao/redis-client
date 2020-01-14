package com.jean.redis.client.task;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.model.RedisPoolWrapper;
import com.jean.redis.client.model.RedisServerProperty;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.support.ConnectionPoolSupport;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class RedisConnectionPoolTask extends BaseTask<RedisPoolWrapper> {

    public RedisConnectionPoolTask(RedisServerProperty serverProperty, EventHandler<WorkerStateEvent> eventHandler) {
        super(serverProperty, eventHandler);
    }

    @Override
    protected RedisPoolWrapper call() throws Exception {
        RedisURI.Builder builder = RedisURI.builder()
                .withHost(serverProperty.getHost())
                .withPort(serverProperty.getPort());
        if (serverProperty.getPassword() != null) {
            builder.withPassword(serverProperty.getPassword());
        }
        RedisURI redisURI = builder.build();
        RedisClient client = RedisClient.create(redisURI);
        CommonConstant.GLOBAL_REDIS_CLIENT_CACHE.put(serverProperty.getUuid(), client);
        GenericObjectPoolConfig<StatefulRedisConnection<byte[], byte[]>> poolConfig = new GenericObjectPoolConfig<>();
        GenericObjectPool<StatefulRedisConnection<byte[], byte[]>> pool = ConnectionPoolSupport.createGenericObjectPool(() -> client.connect(ByteArrayCodec.INSTANCE), poolConfig, true);
        StatefulRedisConnection<byte[], byte[]> connection = pool.borrowObject();
        RedisCommands<byte[], byte[]> commands = connection.sync();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            try {
                commands.select(i);
            } catch (Throwable ignored) {
                connection.close();
                return new RedisPoolWrapper(pool, i);
            }
        }
        throw new Exception("db index too long");
    }

    @Override
    public String toString() {
        return "connect-task-" + super.toString();
    }
}
