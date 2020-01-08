package com.jean.redis.client.item;

import com.jean.redis.client.handler.RedisServerItemMenuActionHandler;
import com.jean.redis.client.model.RedisServerProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

public class RedisServerItem extends TreeItem implements ContextMenuable {

    private final ContextMenu contextMenu;


    public RedisServerItem(RedisServerProperty serverProperty,
                           RedisServerItemMenuActionHandler openConnectionActionEventHandler,
                           RedisServerItemMenuActionHandler closeConnectionActionEventHandler,
                           RedisServerItemMenuActionHandler deleteConnectionActionEventHandler,
                           RedisServerItemMenuActionHandler connectionPropertyActionEventHandler) {
        super(serverProperty.toString());
        MenuItem openItem = new MenuItem("打开连接");
        openItem.disableProperty().bind(expandedProperty());
        openItem.setOnAction(event -> openConnectionActionEventHandler.handler(RedisServerItem.this, openItem, serverProperty));

        MenuItem closeItem = new MenuItem("关闭连接");
        closeItem.disableProperty().bind(expandedProperty().not());
        closeItem.setOnAction(event -> closeConnectionActionEventHandler.handler(RedisServerItem.this, closeItem, serverProperty));

        MenuItem propertyItem = new MenuItem("连接属性");
        propertyItem.disableProperty().bind(expandedProperty().not());
        propertyItem.setOnAction(event -> connectionPropertyActionEventHandler.handler(RedisServerItem.this, propertyItem, serverProperty));

        MenuItem deleteItem = new MenuItem("删除连接");
        deleteItem.setOnAction(event -> deleteConnectionActionEventHandler.handler(RedisServerItem.this, deleteItem, serverProperty));
        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(openItem, closeItem, propertyItem, deleteItem);

    }

    @Override
    public ContextMenu getContextMenu() {
        return this.contextMenu;
    }

}
