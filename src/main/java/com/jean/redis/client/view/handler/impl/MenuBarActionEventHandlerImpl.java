package com.jean.redis.client.view.handler.impl;

import com.jean.redis.client.dialog.CreateRedisServerDialog;
import com.jean.redis.client.model.RedisServerProperty;
import com.jean.redis.client.util.ViewUtils;
import com.jean.redis.client.view.RedisServerItem;
import com.jean.redis.client.view.handler.IMenuBarActionEventHandler;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeView;

/**
 * @author jinshubao
 */
public class MenuBarActionEventHandlerImpl implements IMenuBarActionEventHandler {

    private final TreeView<Object> serverTreeView;

    public MenuBarActionEventHandlerImpl() {
        this.serverTreeView = ViewUtils.getInstance().getServerTreeView();
    }

    @Override
    public void create() {
        connectionDialog().showAndWait().ifPresent(property -> {
            RedisServerItem serverItem = new RedisServerItem(property, new RedisServerItemActionEventHandler());
            serverItem.setExpanded(false);
            serverTreeView.getRoot().getChildren().add(serverItem);
        });
    }

    @Override
    public void exit() {
        Platform.exit();
    }

    @Override
    public void about() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Redis客户端工具");
        alert.setContentText("暂不支持集群模式和哨兵模式");
        alert.show();
    }

    private CreateRedisServerDialog connectionDialog() {
        RedisServerProperty property = new RedisServerProperty();
        property.setHost("rancher.jean.com");
        property.setPort(63790);
        return CreateRedisServerDialog.newInstance(property);
    }
}
