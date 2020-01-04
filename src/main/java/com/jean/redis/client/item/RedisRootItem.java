package com.jean.redis.client.item;

import com.jean.redis.client.dialog.CreateRedisServerDialog;
import com.jean.redis.client.model.RedisServerProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

public class RedisRootItem extends TreeItem implements ContextMenuable {

    private final ContextMenu contextMenu;

    public RedisRootItem(String value) {
        super(value);
        MenuItem newServer = new MenuItem("添加服务器");
        newServer.setOnAction(t -> this.addNewServer());
        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(newServer);
    }

    private void addNewServer() {
        this.createDialog().showAndWait().ifPresent(property -> getChildren().add(new RedisServerItem(property)));
    }

    private CreateRedisServerDialog createDialog() {
        RedisServerProperty property = new RedisServerProperty();
        property.setHost("101.132.156.127");
        property.setPort(6379);
        property.setPassword("123!=-09][po");
        return CreateRedisServerDialog.newInstance(property);
    }

    @Override
    public ContextMenu getContextMenu() {
        return this.contextMenu;
    }
}
