package com.jean.redis.client.item;

import com.jean.redis.client.handler.RedisServerItemActionEventHandler;
import com.jean.redis.client.model.RedisServerProperty;
import com.jean.redis.client.util.ResourceLoader;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class RedisServerItem extends TreeItem<Object> implements Menuable, MouseClickable {

    private BooleanProperty open = new SimpleBooleanProperty(this, "open", false);

    private final ContextMenu contextMenu;

    private final EventHandler<MouseEvent> mouseEventEventHandler;

    private final RedisServerProperty serverProperty;

    private final RedisServerItemActionEventHandler handler;

    public RedisServerItem(RedisServerProperty serverProperty, RedisServerItemActionEventHandler handler) {
        super(serverProperty.toString());
        this.serverProperty = serverProperty;
        this.handler = handler;

        MenuItem openItem = new MenuItem("打开连接", new ImageView(new Image(ResourceLoader.Image.connect_16)));
        openItem.disableProperty().bind(openProperty());
        openItem.setOnAction(event -> this.handler.open(event, RedisServerItem.this, this.serverProperty));

        MenuItem closeItem = new MenuItem("关闭连接", new ImageView(new Image(ResourceLoader.Image.disconnect_16)));
        closeItem.disableProperty().bind(openProperty().not());
        closeItem.setOnAction(event -> this.handler.close(event, RedisServerItem.this, this.serverProperty));

        MenuItem propertyItem = new MenuItem("连接属性");
        propertyItem.disableProperty().bind(openProperty().not());
        propertyItem.setOnAction(event -> this.handler.property(event, RedisServerItem.this, this.serverProperty));

        MenuItem deleteItem = new MenuItem("删除连接", new ImageView(new Image(ResourceLoader.Image.delete_16)));
        deleteItem.setOnAction(event -> this.handler.delete(event, RedisServerItem.this, this.serverProperty));
        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(openItem, closeItem, propertyItem, deleteItem);

        mouseEventEventHandler = event -> handler.click(event, this, this.serverProperty);

        setGraphic(new ImageView(new Image(ResourceLoader.Image.server_16)));
    }

    @Override
    public ContextMenu getContextMenu() {
        return this.contextMenu;
    }

    @Override
    public EventHandler<MouseEvent> getClickEventHandler() {
        return this.mouseEventEventHandler;
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
}
