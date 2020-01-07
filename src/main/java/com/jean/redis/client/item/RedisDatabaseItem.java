package com.jean.redis.client.item;

import com.jean.redis.client.handler.RedisDatabaseItemMenuActionHandler;
import com.jean.redis.client.model.RedisServerProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

public class RedisDatabaseItem extends TreeItem implements ContextMenuable {

    private final ContextMenu contextMenu;

    private final RedisServerProperty serverProperty;

    private final int database;

    public RedisDatabaseItem(RedisServerProperty serverProperty, int database,
                             RedisDatabaseItemMenuActionHandler refreshEventHandler,
                             RedisDatabaseItemMenuActionHandler flushEventHandler) {
        super("db" + database);
        this.serverProperty = serverProperty;
        this.database = database;

        MenuItem refreshItem = new MenuItem("刷新");
        refreshItem.setOnAction(event -> refreshEventHandler.handler(RedisDatabaseItem.this, refreshItem, serverProperty, database));

        MenuItem flushItem = new MenuItem("清空");
        flushItem.setOnAction(event -> flushEventHandler.handler(RedisDatabaseItem.this, refreshItem, serverProperty, database));

        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(refreshItem, flushItem);
    }

    @Override
    public ContextMenu getContextMenu() {
        return this.contextMenu;
    }

    public RedisServerProperty getServerProperty() {
        return serverProperty;
    }

    public int getDatabase() {
        return database;
    }
}
