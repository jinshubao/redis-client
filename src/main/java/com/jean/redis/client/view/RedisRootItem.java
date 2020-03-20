package com.jean.redis.client.view;

import com.jean.redis.client.util.ResourceLoader;
import com.jean.redis.client.view.action.IContextMenu;
import com.jean.redis.client.view.action.IMouseAction;
import com.jean.redis.client.view.handler.IMouseEventHandler;
import com.jean.redis.client.view.handler.IRedisRootItemActionEventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author jinshubao
 */
public class RedisRootItem extends TreeItem<Object> implements IContextMenu, IMouseAction {

    private final IRedisRootItemActionEventHandler redisRootItemActionEventHandler;
    private final ContextMenu contextMenu;

    public RedisRootItem(String value, IRedisRootItemActionEventHandler redisRootItemActionEventHandler) {
        super(value);
        this.redisRootItemActionEventHandler = redisRootItemActionEventHandler;
        MenuItem newServer = new MenuItem("添加服务器", new ImageView(new Image(ResourceLoader.Image.add_16)));
        newServer.setOnAction(event -> redisRootItemActionEventHandler.create(this));
        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(newServer);
    }

    @Override
    public ContextMenu getContextMenu() {
        return this.contextMenu;
    }

    @Override
    public IMouseEventHandler getMouseEventHandler() {
        return this.redisRootItemActionEventHandler;
    }
}
