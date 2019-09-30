package com.jean.redis.client.Service;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.model.ConfigProperty;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.StringCodec;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
public abstract class BaseService<V> extends Service {

    protected ConfigProperty config;

    public ConfigProperty getConfig() {
        return config;
    }

    protected static abstract class RedisTask<V> extends Task<V> {

        protected Logger logger = LoggerFactory.getLogger(this.getClass());

        protected final ConfigProperty config;

        public RedisTask(ConfigProperty config) {
            this.config = config;
        }

        protected StatefulRedisConnection<String, String> getRedisConnection() {
            RedisClient redisClient = CommonConstant.REDIS_CLIENT_MAP.get(config.toString());
            if (redisClient != null) {
                return redisClient.connect(StringCodec.UTF8);
            }
            RedisURI.Builder builder = RedisURI.Builder.redis(config.getHost(), config.getPort()).withDatabase(config.getDatabase());
            if (config.getPassword() != null) {
                builder.withPassword(config.getPassword());
            }
            RedisClient client = RedisClient.create(builder.build());
            CommonConstant.REDIS_CLIENT_MAP.put(config.toString(), client);
            return this.getRedisConnection();
        }


        @Override
        protected void done() {
            updateMessage("执行完成.");
        }

        @Override
        protected void failed() {
            updateMessage("执行失败...");
        }

        @Override
        protected void succeeded() {
            updateMessage("执行成功.");
        }
    }
}
