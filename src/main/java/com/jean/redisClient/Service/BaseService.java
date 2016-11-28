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
        logger.info("增加参数[" + key + ":" + value + "]");
    }

    private void clearParams() {
        params.clear();
        logger.info("清理参数...");
    }

    private void closeRedis() {
        if (jedis != null) {
            logger.info("关闭redis连接..." + jedis.quit());
        }
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
        logger.info("获取redis连接...");
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
                getConnection();
                return task();
            }

            @Override
            protected void done() {
                super.done();
                clearParams();
                closeRedis();
            }
        };
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        logger.info("任务执行成功...");
    }

    @Override
    protected void failed() {
        super.failed();
        logger.warn("任务执行失败...", getException());
    }
}
