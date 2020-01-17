package com.jean.redis.client.task;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.model.RedisServerProperty;
import com.jean.redis.client.util.MessageUtils;
import com.jean.redis.client.util.StringUtils;
import io.lettuce.core.api.StatefulRedisConnection;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.UUID;

public abstract class BaseTask<V> extends Task<V> {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final RedisServerProperty serverProperty;

    private final String taskId;
    private final String taskType;

    BaseTask(RedisServerProperty serverProperty) {
        this.serverProperty = serverProperty;
        this.taskId = UUID.randomUUID().toString();
        this.taskType = this.getClass().getName();
    }

    @Override
    protected void scheduled() {
        updateMessage("开始执行...");
        logger.debug("task[{}] scheduled", this);
    }

    @Override
    protected void cancelled() {
        updateMessage("执行取消.");
        logger.debug("task[{}] cancelled", this);
    }

    @Override
    protected void done() {

    }

    @Override
    protected void failed() {
        updateMessage("执行失败...");
        logger.debug("task[{}] failed", this);
        Throwable exception = getException();
        if (exception != null) {
            logger.error(exception.getMessage(), exception);
            new Alert(Alert.AlertType.ERROR, exception.getMessage(), ButtonType.CLOSE).showAndWait();
        }
    }

    @Override
    protected void succeeded() {
        updateMessage("执行成功.");
        logger.debug("task[{}] succeeded", this);
    }

    @Override
    protected void updateMessage(String message) {
        super.updateMessage(message);
        String title = getTitle();
        String msg = StringUtils.join(Arrays.asList(title, message), "：");
        MessageUtils.updateMessage(msg);
    }

    StatefulRedisConnection<byte[], byte[]> getConnection() throws Exception {
        return CommonConstant.getConnectionPool(serverProperty.getUuid()).borrowObject();
    }

    public String getTaskType() {
        return taskType;
    }

    @Override
    public String toString() {
        return taskId;
    }
}
