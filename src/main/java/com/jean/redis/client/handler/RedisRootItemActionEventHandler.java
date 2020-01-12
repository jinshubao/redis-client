package com.jean.redis.client.handler;

import javafx.event.ActionEvent;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;

/**
 * 根节点事件处理器
 */
public interface RedisRootItemActionEventHandler {

    /**
     * 点击
     *
     * @param mouseEvent event
     * @param treeItem   item
     */
    void click(MouseEvent mouseEvent, TreeItem<?> treeItem);


    /**
     * 新增服务器
     *
     * @param actionEvent event
     */
    void create(ActionEvent actionEvent, TreeItem<?> treeItem);


}
