package com.jean.redis.client.Service;

import com.jean.redis.client.callback.TaskCallback;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
public abstract class BaseService<V> extends Service {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void executor(ThreadPoolTaskExecutor executor) {
        this.setExecutor(executor);
    }

    protected String hostName;
    protected Integer dbIndex;
    protected Integer port;
    protected String auth;

    protected Jedis jedis;


    private TaskCallback<V> successCallback;

    private TaskCallback<Throwable> failedCallback;


    protected Map<String, Object> params = new HashMap<>();

    public void setParams(Map<String, Object> params) {
        this.params.putAll(params);
    }

    public void addParams(String key, Object value) {
        this.params.put(key, value);
    }

    private void clearParams() {
        params.clear();
    }

    private void closeRedis() {
        if (jedis != null) {
            jedis.close();
        }
    }

    protected abstract V task();

    private void getConnection() {
        jedis = new Jedis(hostName, port);
        if (auth != null) {
            jedis.auth(auth);
        }
        if (dbIndex != null) {
            jedis.select(dbIndex);
        } else {
            jedis.select(0);
        }
    }

    @Override
    protected Task<V> createTask() {
        return new Task<V>() {
            @Override
            protected V call() throws Exception {
                updateMessage("获取连接...");
                getConnection();
                updateMessage("开始执行任务...");
                return task();
            }

            @Override
            protected void done() {
                super.done();
                clean();
                clearParams();
                closeRedis();
            }

            @Override
            protected void failed() {
                super.failed();
                updateMessage("任务执行失败...");
                Throwable exception = getException();
                logger.error(exception.getMessage(), exception);
                if (failedCallback != null) {
                    failedCallback.call(exception);
                    failedCallback = null;
                }
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                updateMessage("任务执行成功");
                if (successCallback != null) {
                    successCallback.call(getValue());
                }
                successCallback = null;
            }
        };
    }

    @Override
    public void restart() {
        if (!isRunning()) {
            super.restart();
        } else {
            logger.info("不能重复执行任务...");
        }
    }

    protected void clean() {
    }

    protected void setSuccessCallback(TaskCallback<V> successCallback) {
        this.successCallback = successCallback;
    }

    protected void setFailedCallback(TaskCallback<Throwable> failedCallback) {
        this.failedCallback = failedCallback;
    }
}
