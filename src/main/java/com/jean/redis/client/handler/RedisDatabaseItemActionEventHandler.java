package com.jean.redis.client.handler;

import com.jean.redis.client.model.RedisServerProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;

public interface RedisDatabaseItemActionEventHandler {

    /**
     * 点击
     *
     * @param mouseEvent     event
     * @param treeItem       treeItem
     * @param serverProperty property
     * @param database       db
     */
    void click(MouseEvent mouseEvent, TreeItem<?> treeItem, RedisServerProperty serverProperty, int database);


    /**
     * 刷新
     *
     * @param actionEvent    event
     * @param treeItem       treeItem
     * @param serverProperty property
     * @param database       db
     */
    void refresh(ActionEvent actionEvent, TreeItem<?> treeItem, RedisServerProperty serverProperty, int database);

    /**
     * 清空
     *
     * @param actionEvent    event
     * @param treeItem       treeItem
     * @param serverProperty property
     * @param database       db
     */
    void flush(ActionEvent actionEvent, TreeItem<?> treeItem, RedisServerProperty serverProperty, int database);


}
