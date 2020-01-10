package com.jean.redis.client.handler;

import com.jean.redis.client.item.RedisServerItem;
import com.jean.redis.client.model.RedisServerProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;

public interface RedisServerItemActionEventHandler {


    void click(MouseEvent mouseEvent, RedisServerItem treeItem, RedisServerProperty serverProperty);

    /**
     * @param event          event
     * @param treeItem       treeItem
     * @param serverProperty server
     */
    void open(ActionEvent event, RedisServerItem treeItem, RedisServerProperty serverProperty);

    /**
     * @param event          event
     * @param treeItem       treeItem
     * @param serverProperty server
     */
    void close(ActionEvent event, RedisServerItem treeItem, RedisServerProperty serverProperty);

    /**
     * @param event          event
     * @param treeItem       treeItem
     * @param serverProperty server
     */
    void delete(ActionEvent event, RedisServerItem treeItem, RedisServerProperty serverProperty);

    /**
     * @param event          event
     * @param treeItem       treeItem
     * @param serverProperty server
     */
    void property(ActionEvent event, RedisServerItem treeItem, RedisServerProperty serverProperty);


}
