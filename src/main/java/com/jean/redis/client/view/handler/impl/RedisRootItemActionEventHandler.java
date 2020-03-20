package com.jean.redis.client.view.handler.impl;

import com.jean.redis.client.dialog.CreateRedisServerDialog;
import com.jean.redis.client.model.RedisServerProperty;
import com.jean.redis.client.util.NodeUtils;
import com.jean.redis.client.view.RedisRootItem;
import com.jean.redis.client.view.RedisServerItem;
import com.jean.redis.client.view.handler.IRedisRootItemActionEventHandler;
import com.jean.redis.client.view.handler.IRedisServerItemActionEventHandler;
import javafx.scene.Node;
import javafx.scene.control.TreeView;

/**
 * @author jinshubao
 */
public class RedisRootItemActionEventHandler implements IRedisRootItemActionEventHandler {

    private final TreeView<Object> serverTreeView;
    private final IRedisServerItemActionEventHandler redisServerItemActionEventHandler;

    public RedisRootItemActionEventHandler(Node root) {
        this.serverTreeView = NodeUtils.lookup(root, "#serverTreeView");
        this.redisServerItemActionEventHandler = new RedisServerItemActionEventHandler(root);
    }

    @Override
    public void create(RedisRootItem redisRootItem) {
        connectionDialog().showAndWait().ifPresent(property -> {
            RedisServerItem serverItem = new RedisServerItem(property, redisServerItemActionEventHandler);
            serverTreeView.getRoot().getChildren().add(serverItem);
            serverItem.setExpanded(false);
        });
    }

    private CreateRedisServerDialog connectionDialog() {
        RedisServerProperty property = new RedisServerProperty();
        property.setHost("localhost");
        property.setPort(6379);
        property.setPassword("");
        return CreateRedisServerDialog.newInstance(property);
    }
}
