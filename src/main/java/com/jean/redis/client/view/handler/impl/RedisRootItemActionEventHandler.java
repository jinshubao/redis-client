package com.jean.redis.client.view.handler.impl;

import com.jean.redis.client.dialog.CreateRedisServerDialog;
import com.jean.redis.client.view.handler.BaseMouseEventHandler;
import com.jean.redis.client.view.handler.IRedisRootItemActionEventHandler;
import com.jean.redis.client.view.handler.IRedisServerItemActionEventHandler;
import com.jean.redis.client.view.RedisRootItem;
import com.jean.redis.client.view.RedisServerItem;
import com.jean.redis.client.model.RedisServerProperty;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeView;

public class RedisRootItemActionEventHandler extends BaseMouseEventHandler<RedisRootItem> implements IRedisRootItemActionEventHandler {

    private final TreeView<Object> serverTreeView;

    private final IRedisServerItemActionEventHandler redisServerItemActionEventHandler;

    public RedisRootItemActionEventHandler(Node root) {
        super(root);
        SplitPane splitPane = (SplitPane) root.lookup("#splitPane");
        this.serverTreeView = (TreeView<Object>) splitPane.getItems().stream().filter(item -> "serverTreeView".equals(item.getId())).findFirst().orElseThrow(() -> new RuntimeException("id='serverTreeView' not fund"));

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
        property.setHost("redis.jean.com");
        property.setPort(6379);
        property.setPassword("");
        return CreateRedisServerDialog.newInstance(property);
    }
}
