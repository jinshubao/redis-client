package com.jean.redis.client.item;

import com.jean.redis.client.handler.RedisDatabaseItemActionEventHandler;
import com.jean.redis.client.model.RedisServerProperty;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;

public class RedisDatabaseItem extends TreeItem<Object> implements Menuable, MouseClickable {

    private final ContextMenu contextMenu;

    private final EventHandler<MouseEvent> mouseEventEventHandler;

    private final RedisServerProperty serverProperty;

    private final int database;

    private final RedisDatabaseItemActionEventHandler handler;

    public RedisDatabaseItem(RedisServerProperty serverProperty, int database, RedisDatabaseItemActionEventHandler handler) {
        super("db" + database);
        this.serverProperty = serverProperty;
        this.database = database;
        this.handler = handler;

        MenuItem refreshItem = new MenuItem("刷新");
        refreshItem.setOnAction(event -> this.handler.refresh(event, RedisDatabaseItem.this, this.serverProperty, this.database));

        MenuItem flushItem = new MenuItem("清空");
        flushItem.setOnAction(event -> this.handler.flush(event, RedisDatabaseItem.this, this.serverProperty, this.database));

        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(refreshItem, flushItem);

        mouseEventEventHandler = event -> handler.click(event, this, this.serverProperty, this.database);
    }

    @Override
    public ContextMenu getContextMenu() {
        return this.contextMenu;
    }


    @Override
    public EventHandler<MouseEvent> getClickEventHandler() {
        return this.mouseEventEventHandler;
    }

    public RedisServerProperty getServerProperty() {
        return serverProperty;
    }

    public int getDatabase() {
        return database;
    }
}
