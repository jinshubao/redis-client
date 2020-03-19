package com.jean.redis.client.view;

import com.jean.redis.client.view.handler.IRedisRootItemActionEventHandler;
import com.jean.redis.client.util.ResourceLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RedisRootItem extends BaseTreeItem<Object> {

    private final ContextMenu contextMenu;

    public RedisRootItem(String value, IRedisRootItemActionEventHandler redisRootItemActionEventHandler) {
        super(value, redisRootItemActionEventHandler);

        MenuItem newServer = new MenuItem("添加服务器", new ImageView(new Image(ResourceLoader.Image.add_16)));
        newServer.setOnAction(event -> redisRootItemActionEventHandler.create(this));
        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(newServer);
    }

    @Override
    public ContextMenu getContextMenu() {
        return this.contextMenu;
    }
}
