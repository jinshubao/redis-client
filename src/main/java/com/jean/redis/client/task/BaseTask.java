package com.jean.redis.client.task;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.model.RedisServerProperty;
import io.lettuce.core.api.StatefulRedisConnection;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.apache.commons.pool2.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTask<V> extends Task<V> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final RedisServerProperty serverProperty;

    public BaseTask(RedisServerProperty serverProperty) {
        this.serverProperty = serverProperty;
    }

    @Override
    protected void cancelled() {
        updateMessage("执行取消.");
    }

    @Override
    protected void done() {
        updateMessage("执行完成.");
    }

    @Override
    protected void failed() {
        updateMessage("执行失败...");
        Throwable exception = getException();
        if (exception != null) {
            logger.error(exception.getMessage(), exception);
            new Alert(Alert.AlertType.ERROR, exception.getMessage(), ButtonType.CLOSE).showAndWait();
        }
    }

    @Override
    protected void succeeded() {
        updateMessage("执行成功.");
    }

    protected ObjectPool<StatefulRedisConnection<byte[], byte[]>> getConnectionObjectPool() {
        return CommonConstant.getConnectionPool(serverProperty.getUuid());
    }

    protected StatefulRedisConnection<byte[], byte[]> getConnection() throws Exception {
        return getConnectionObjectPool().borrowObject();
    }

}
