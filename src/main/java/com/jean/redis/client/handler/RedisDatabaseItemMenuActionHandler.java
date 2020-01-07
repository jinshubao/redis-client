package com.jean.redis.client.handler;

import com.jean.redis.client.model.RedisServerProperty;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

@FunctionalInterface
public interface RedisDatabaseItemMenuActionHandler {


    /**
     * @param treeItem
     * @param menuItem
     * @param serverProperty
     * @param database
     */
    void handler(TreeItem treeItem, MenuItem menuItem, RedisServerProperty serverProperty, int database);


}
