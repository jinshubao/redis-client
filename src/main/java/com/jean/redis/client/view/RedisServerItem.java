package com.jean.redis.client.view;

import com.jean.redis.client.view.handler.IRedisServerItemActionEventHandler;
import com.jean.redis.client.model.RedisServerProperty;
import com.jean.redis.client.util.ResourceLoader;
import com.jean.redis.client.view.action.IContextMenu;
import com.jean.redis.client.view.action.IMouseAction;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RedisServerItem extends BaseTreeItem<Object> implements IContextMenu, IMouseAction {

    private BooleanProperty open = new SimpleBooleanProperty(this, "open", false);

    private final ContextMenu contextMenu;

    private final RedisServerProperty serverProperty;

    private final IRedisServerItemActionEventHandler actionEventHandler;

    public RedisServerItem(RedisServerProperty serverProperty, IRedisServerItemActionEventHandler actionEventHandler) {
        super(serverProperty.toString(), actionEventHandler);
        this.serverProperty = serverProperty;
        this.actionEventHandler = actionEventHandler;

        MenuItem openItem = new MenuItem("打开连接", new ImageView(new Image(ResourceLoader.Image.connect_16)));
        openItem.disableProperty().bind(openProperty());
        openItem.setOnAction(event -> this.actionEventHandler.open(this));

        MenuItem closeItem = new MenuItem("关闭连接", new ImageView(new Image(ResourceLoader.Image.disconnect_16)));
        closeItem.disableProperty().bind(openProperty().not());
        closeItem.setOnAction(event -> this.actionEventHandler.close(this));

        MenuItem propertyItem = new MenuItem("连接属性");
        propertyItem.disableProperty().bind(openProperty().not());
        propertyItem.setOnAction(event -> this.actionEventHandler.property(this));

        MenuItem deleteItem = new MenuItem("删除连接", new ImageView(new Image(ResourceLoader.Image.delete_16)));
        deleteItem.setOnAction(event -> this.actionEventHandler.delete(this));
        this.contextMenu = new ContextMenu();
        this.contextMenu.getItems().addAll(openItem, closeItem, propertyItem, deleteItem);

        this.setGraphic(new ImageView(new Image(ResourceLoader.Image.server_16)));
    }

    @Override
    public ContextMenu getContextMenu() {
        return this.contextMenu;
    }

    public boolean isOpen() {
        return open.get();
    }

    public BooleanProperty openProperty() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open.set(open);
    }

    public RedisServerProperty getServerProperty() {
        return serverProperty;
    }
}
