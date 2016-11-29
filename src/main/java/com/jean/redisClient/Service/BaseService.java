package com.jean.redisClient.Service;

import com.jean.redisClient.model.DbModel;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by jinshubao on 2016/11/25.
 */
public abstract class BaseService<V> extends Service {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void executor(Executor executor) {
        this.setExecutor(executor);
    }

    private DbModel dbModel;

    public void setDbModel(DbModel dbModel) {
        this.dbModel = dbModel;
    }

    protected Jedis jedis;

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

    private String closeRedis() {
        if (jedis != null) {
            return jedis.quit();
        }
        return "";
    }


    private void printCurrentThreadName() {
        //logger.info(Thread.currentThread().getName());
    }

    @Override
    public void restart() {
        if (!isRunning()) {
            super.restart();
        } else {
            logger.warn("任务正在执行不能重复执行...");
        }
    }

    public abstract V task();

    private void getConnection() {
        jedis = new Jedis(dbModel.getHostName(), dbModel.getPort());
        if (dbModel.getAuth() != null) {
            jedis.auth(dbModel.getAuth());
        }
        if (dbModel.getDbIndex() != null) {
            jedis.select(dbModel.getDbIndex());
        } else {
            jedis.select(0);
        }
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                printCurrentThreadName();
                updateMessage("获取连接...");
                getConnection();
                updateMessage("开始执行任务...");
                return task();
            }

            @Override
            protected void done() {
                super.done();
                clearParams();
                closeRedis();
            }

            @Override
            protected void failed() {
                super.failed();
                updateMessage("任务执行失败...");
                Throwable exception = getException();
                logger.error(exception.getMessage(), exception);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                updateMessage("任务执行成功...");
            }
        };
    }
}
