package com.jean.redis.client.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.UUID;

public class RedisServerProperty {


    private StringProperty host = new SimpleStringProperty(this, "host");
    private IntegerProperty port = new SimpleIntegerProperty(this, "port");
    private StringProperty password = new SimpleStringProperty(this, "password");
    private StringProperty uuid = new SimpleStringProperty(this, "uuid");

    public RedisServerProperty() {
        this.uuid.set(UUID.randomUUID().toString());
    }

    public String getHost() {
        return host.get();
    }

    public StringProperty hostProperty() {
        return host;
    }

    public void setHost(String host) {
        this.host.set(host);
    }

    public int getPort() {
        return port.get();
    }

    public IntegerProperty portProperty() {
        return port;
    }

    public void setPort(int port) {
        this.port.set(port);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getUuid() {
        return uuid.get();
    }

    public StringProperty uuidProperty() {
        return uuid;
    }

    @Override
    public String toString() {
        return this.getHost() + ":" + this.getPort();
    }
}
