package com.jean.redis.client.handler;

import javafx.event.ActionEvent;

/**
 * 菜单栏事件处理器
 */
public interface MenuBarActionEventHandler {

    void create(ActionEvent actionEvent);

    void exit(ActionEvent actionEvent);

    void about(ActionEvent actionEvent);
}
