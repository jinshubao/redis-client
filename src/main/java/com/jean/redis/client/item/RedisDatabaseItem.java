package com.jean.redis.client.item;

import com.jean.redis.client.model.RedisDatabaseProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

public class RedisDatabaseItem extends TreeItem implements ContextMenuable {

    private final ContextMenu contextMenu;

    public RedisDatabaseItem(RedisDatabaseProperty value) {
        super(value);
        MenuItem refreshItem = new MenuItem("刷新");
        MenuItem flushItem = new MenuItem("清空");
        refreshItem.setOnAction(t -> this.refreshDatabase());
        flushItem.setOnAction(event -> this.flushDatabase());
        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(refreshItem, flushItem);
    }

    private void refreshDatabase() {
        //TODO
    }

    private void flushDatabase() {
        //TODO
    }

    @Override
    public ContextMenu getContextMenu() {
        return this.contextMenu;
    }
}
