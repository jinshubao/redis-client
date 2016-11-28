package com.jean.redisClient.model;

/**
 * Created by jinshubao on 2016/11/28.
 */
public class HostModel extends NodeModel {


    String hostName;
    Integer port;
    String auth;

    public HostModel() {
    }

    public HostModel(String hostName, Integer port) {
        this.hostName = hostName;
        this.port = port;
    }

    public HostModel(String hostName, Integer port, String auth) {
        this.hostName = hostName;
        this.port = port;
        this.auth = auth;
    }

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

    @Override
    public String toString() {
        return hostName + ":" + port;
    }
}