package com.jean.redis.client.task;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.model.RedisServerProperty;
import io.lettuce.core.api.StatefulRedisConnection;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseTask<V> extends Task<V> {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final RedisServerProperty serverProperty;

    private final String taskId;

    private static Map<Class, List<Task>> taskCaches = new ConcurrentHashMap<>();

    BaseTask(RedisServerProperty serverProperty, EventHandler<WorkerStateEvent> eventHandler) {
        this.serverProperty = serverProperty;
        this.taskId = UUID.randomUUID().toString();
        if (eventHandler != null) {
            this.addEventHandler(WorkerStateEvent.WORKER_STATE_READY, eventHandler);
            this.addEventHandler(WorkerStateEvent.WORKER_STATE_SCHEDULED, eventHandler);
            this.addEventHandler(WorkerStateEvent.WORKER_STATE_RUNNING, eventHandler);
            this.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, eventHandler);
            this.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, eventHandler);
            this.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, eventHandler);
        }
    }

    @Override
    protected void scheduled() {
        logger.debug("task[{}] scheduled", this);
        List<Task> tasks = taskCaches.get(this.getClass());
        if (tasks == null) {
            List<Task> list = new ArrayList<>();
            list.add(this);
            taskCaches.put(this.getClass(), list);
        } else if (tasks.isEmpty()) {
            tasks.add(this);
        } else {
            List<Task> list = new ArrayList<>(tasks.size());
            list.addAll(tasks);
            for (Task next : list) {
                if (next.isRunning()) {
                    next.cancel(false);
                    logger.debug("cancel task[{}]", next);
                }
            }
        }
    }

    @Override
    protected void cancelled() {
        updateMessage("执行取消.");
        logger.debug("task[{}] cancelled", this);
    }

    @Override
    protected void done() {
        List<Task> tasks = taskCaches.get(this.getClass());
        if (tasks != null && !tasks.isEmpty()) {
            tasks.remove(this);
        }
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

    StatefulRedisConnection<byte[], byte[]> getConnection() throws Exception {
        return CommonConstant.getConnectionPool(serverProperty.getUuid()).borrowObject();
    }

    @Override
    public String toString() {
        return taskId;
    }
}
