package com.jean.redis.client.item;

import com.jean.redis.client.handler.RedisRootItemActionEventHandler;
import com.jean.redis.client.util.ResourceLoader;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class RedisRootItem extends TreeItem<Object> implements Menuable, MouseClickable {

    private final ContextMenu contextMenu;

    private final EventHandler<MouseEvent> mouseEventEventHandler;

    public RedisRootItem(String value, RedisRootItemActionEventHandler handler) {
        super(value);
        MenuItem newServer = new MenuItem("添加服务器", new ImageView(new Image(ResourceLoader.Image.add_16)));
        newServer.setOnAction(event -> handler.create(event, this));
        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(newServer);
        mouseEventEventHandler = event -> handler.click(event, this);
    }

    @Override
    public ContextMenu getContextMenu() {
        return this.contextMenu;
    }

    @Override
    public EventHandler<MouseEvent> getClickEventHandler() {
        return this.mouseEventEventHandler;
    }
}
