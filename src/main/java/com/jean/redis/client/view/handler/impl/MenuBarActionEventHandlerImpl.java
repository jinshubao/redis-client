package com.jean.redis.client.view.handler.impl;

import com.jean.redis.client.dialog.CreateRedisServerDialog;
import com.jean.redis.client.view.handler.BaseEventHandler;
import com.jean.redis.client.view.handler.IMenuBarActionEventHandler;
import com.jean.redis.client.view.RedisServerItem;
import com.jean.redis.client.model.RedisServerProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeView;

public class MenuBarActionEventHandlerImpl extends BaseEventHandler implements IMenuBarActionEventHandler {

    public TreeView<Object> serverTreeView;

    public MenuBarActionEventHandlerImpl(Node root) {
        super(root);
        SplitPane splitPane = (SplitPane) root.lookup("#splitPane");
        this.serverTreeView = (TreeView<Object>) splitPane.getItems().stream().filter(item -> "serverTreeView".equals(item.getId())).findFirst().orElseThrow(() -> new RuntimeException("id='serverTreeView' not fund"));
    }

    @Override
    public void create(ActionEvent actionEvent) {
        connectionDialog().showAndWait().ifPresent(property -> {
            RedisServerItem serverItem = new RedisServerItem(property, new RedisServerItemActionEventHandler(getParent()));
            serverItem.setExpanded(false);
            serverTreeView.getRoot().getChildren().add(serverItem);
        });
    }

    @Override
    public void exit(ActionEvent actionEvent) {
        System.exit(0);
    }

    @Override
    public void about(ActionEvent actionEvent) {
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
//        property.setPassword("123!=-09][po");
        return CreateRedisServerDialog.newInstance(property);
    }
}
