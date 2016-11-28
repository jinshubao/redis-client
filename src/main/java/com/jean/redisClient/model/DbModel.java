package com.jean.redisClient.model;

/**
 * Created by jinshubao on 2016/11/28.
 */
public class DbModel extends NodeModel {

    public DbModel(String hostName, Integer port, String auth, Integer dbIndex) {
        this.hostName = hostName;
        this.port = port;
        this.auth = auth;
        this.dbIndex = dbIndex;
    }

    public DbModel(HostModel hostModel, Integer dbIndex) {
        this(hostModel.hostName, hostModel.port, hostModel.auth, dbIndex);
    }

    private Integer dbIndex;
    private String hostName;
    private Integer port;
    private String auth;


    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }


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
