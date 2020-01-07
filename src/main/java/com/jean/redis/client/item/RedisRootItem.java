package com.jean.redis.client.item;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

public class RedisRootItem extends TreeItem implements ContextMenuable {

    private final ContextMenu contextMenu;


    @SuppressWarnings("unchecked")
    public RedisRootItem(String value, EventHandler<ActionEvent> eventEventHandler) {
        super(value);
        MenuItem newServer = new MenuItem("添加服务器");
        newServer.setOnAction(eventEventHandler);
        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(newServer);
    }

    @Override
    public ContextMenu getContextMenu() {
        return this.contextMenu;
    }
}
