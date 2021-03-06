package com.jean.redis.client.view;

import com.jean.redis.client.model.RedisServerProperty;
import com.jean.redis.client.util.ResourceLoader;
import com.jean.redis.client.view.action.IContextMenu;
import com.jean.redis.client.view.action.IMouseAction;
import com.jean.redis.client.view.handler.IMouseEventHandler;
import com.jean.redis.client.view.handler.IRedisDatabaseItemActionEventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author jinshubao
 */
public class RedisDatabaseItem extends TreeItem<Object> implements IContextMenu, IMouseAction {


    private final IRedisDatabaseItemActionEventHandler databaseItemActionEventHandler;
    private final RedisServerProperty serverProperty;
    private final int database;
    private final ContextMenu contextMenu;

    public RedisDatabaseItem(RedisServerProperty serverProperty, int database, IRedisDatabaseItemActionEventHandler databaseItemActionEventHandler) {
        super("db" + database);
        this.serverProperty = serverProperty;
        this.database = database;
        this.databaseItemActionEventHandler = databaseItemActionEventHandler;

        MenuItem refreshItem = new MenuItem("刷新", new ImageView(new Image(ResourceLoader.Image.refresh_16)));
        refreshItem.setOnAction(event -> databaseItemActionEventHandler.refresh(this));

        MenuItem flushItem = new MenuItem("清空", new ImageView(new Image(ResourceLoader.Image.refresh_16)));
        flushItem.setOnAction(event -> databaseItemActionEventHandler.flush(this));

        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(refreshItem, flushItem);
    }

    @Override
    public ContextMenu getContextMenu() {
        return this.contextMenu;
    }

    @Override
    public IMouseEventHandler getMouseEventHandler() {
        return this.databaseItemActionEventHandler;
    }

    public RedisServerProperty getServerProperty() {
        return serverProperty;
    }

    public int getDatabase() {
        return database;
    }

}
