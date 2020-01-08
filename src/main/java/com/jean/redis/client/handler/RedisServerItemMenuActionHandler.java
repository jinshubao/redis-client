package com.jean.redis.client.handler;

import com.jean.redis.client.model.RedisServerProperty;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

@FunctionalInterface
public interface RedisServerItemMenuActionHandler {


    /**
     * @param treeItem treeItem
     * @param menuItem menuItem
     * @param serverProperty server
     */
    void handler(TreeItem treeItem, MenuItem menuItem, RedisServerProperty serverProperty);


}
