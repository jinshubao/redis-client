package com.jean.redis.client.item;

import com.jean.redis.client.handler.RedisServerItemMenuActionHandler;
import com.jean.redis.client.model.RedisServerProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

public class RedisServerItem extends TreeItem implements ContextMenuable {

    private BooleanProperty isOpen = new SimpleBooleanProperty(this, "isOpen");

    private final ContextMenu contextMenu;

    private final MenuItem openItem;
    private final MenuItem closeItem;
    private final MenuItem propertyItem;
    private final MenuItem deleteItem;

    public RedisServerItem(RedisServerProperty serverProperty,
                           RedisServerItemMenuActionHandler openConnectionActionEventHandler,
                           RedisServerItemMenuActionHandler closeConnectionActionEventHandler,
                           RedisServerItemMenuActionHandler deleteConnectionActionEventHandler,
                           RedisServerItemMenuActionHandler connectionPropertyActionEventHandler) {
        super(serverProperty.toString());
        openItem = new MenuItem("打开连接");
        openItem.disableProperty().bind(isOpen);
        openItem.setOnAction(event -> openConnectionActionEventHandler.handler(RedisServerItem.this, serverProperty));

        closeItem = new MenuItem("关闭连接");
        closeItem.disableProperty().bind(isOpen.not());
        closeItem.setOnAction(event -> closeConnectionActionEventHandler.handler(RedisServerItem.this, serverProperty));

        propertyItem = new MenuItem("连接属性");
        propertyItem.disableProperty().bind(isOpen.not());
        propertyItem.setOnAction(event -> connectionPropertyActionEventHandler.handler(RedisServerItem.this, serverProperty));

        deleteItem = new MenuItem("删除连接");
        deleteItem.setOnAction(event -> deleteConnectionActionEventHandler.handler(RedisServerItem.this, serverProperty));
        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(openItem, closeItem, propertyItem, deleteItem);
    }

    @Override
    public ContextMenu getContextMenu() {
        return this.contextMenu;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen.set(isOpen);
    }

    public boolean isIsOpen() {
        return isOpen.get();
    }

    public BooleanProperty isOpenProperty() {
        return isOpen;
    }
}
