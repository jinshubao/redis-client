package com.jean.redis.client.view.handler;

import javafx.event.ActionEvent;

/**
 * 菜单栏事件处理器
 */
public interface IMenuBarActionEventHandler {

    void create(ActionEvent actionEvent);

    void exit(ActionEvent actionEvent);

    void about(ActionEvent actionEvent);
}
