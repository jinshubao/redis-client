package com.jean.redisClient.model;

/**
 * Created by jinshubao on 2016/11/28.
 */
public class DbModel extends HostModel {

    public DbModel(String hostName, Integer port, String auth, Integer dbIndex) {
        super(hostName, port, auth);
        this.dbIndex = dbIndex;
    }

    public DbModel(HostModel hostModel, Integer dbIndex) {
        this(hostModel.hostName, hostModel.port, hostModel.auth, dbIndex);
    }

    protected Integer dbIndex;

    public Integer getDbIndex() {
        return dbIndex;
    }

    public void setDbIndex(Integer dbIndex) {
        this.dbIndex = dbIndex;
    }

    @Override
    public String toString() {
        return "db" + dbIndex;
    }
}
