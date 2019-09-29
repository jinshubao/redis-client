package com.jean.redis.client.model;

public class ConfigProperty {

    private String host;
    private int port;
    private String password;
    private int database;

    public ConfigProperty() {
    }

    public ConfigProperty(String host, int port, String password, int database) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.database = database;
    }

    public ConfigProperty(ConfigProperty config, int database) {
        this.host = config.host;
        this.port = config.port;
        this.password = config.password;
        this.database = database;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }


    @Override
    public String toString() {
        return this.host + ":" + this.port + ":" + this.database;
    }
}
