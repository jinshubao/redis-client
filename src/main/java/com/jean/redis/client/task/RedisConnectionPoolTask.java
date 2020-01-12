package com.jean.redis.client.task;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.model.RedisServerProperty;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class RedisConnectionPoolTask extends BaseTask<ObjectPool<StatefulRedisConnection<byte[], byte[]>>> {

    public RedisConnectionPoolTask(RedisServerProperty serverProperty) {
        super(serverProperty);
    }

    @Override
    protected ObjectPool<StatefulRedisConnection<byte[], byte[]>> call() throws Exception {
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
        return ConnectionPoolSupport.createGenericObjectPool(() -> client.connect(ByteArrayCodec.INSTANCE), poolConfig, true);
    }
}
