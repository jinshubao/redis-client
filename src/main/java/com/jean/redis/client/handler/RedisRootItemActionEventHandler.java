package com.jean.redis.client.handler;

import javafx.event.ActionEvent;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;

public interface RedisRootItemActionEventHandler {


    void click(MouseEvent mouseEvent, TreeItem treeItem);


    /**
     * 新增服务器
     *
     * @param actionEvent event
     */
    void create(ActionEvent actionEvent, TreeItem treeItem);


}
