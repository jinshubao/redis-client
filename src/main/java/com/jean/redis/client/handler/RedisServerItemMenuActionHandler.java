package com.jean.redis.client.handler;

import com.jean.redis.client.model.RedisServerProperty;
import javafx.scene.control.TreeItem;

@FunctionalInterface
public interface RedisServerItemMenuActionHandler {


    /**
     * @param treeItem
     * @param serverProperty
     */
    void handler(TreeItem treeItem, RedisServerProperty serverProperty);


}
