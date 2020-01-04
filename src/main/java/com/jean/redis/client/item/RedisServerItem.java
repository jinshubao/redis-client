package com.jean.redis.client.item;

import com.jean.redis.client.model.RedisDatabaseProperty;
import com.jean.redis.client.model.RedisServerProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

public class RedisServerItem extends TreeItem implements ContextMenuable {

    private final ContextMenu contextMenu;

    public RedisServerItem(RedisServerProperty value) {
        super(value);
        MenuItem openItem = new MenuItem("打开连接");
        openItem.setOnAction(t -> this.openConnection());
        MenuItem closeItem = new MenuItem("关闭连接");
        closeItem.setOnAction(t -> this.closeConnection());
        MenuItem propertyItem = new MenuItem("连接属性");
        propertyItem.setOnAction(event -> this.connectionProperty());
        MenuItem deleteItem = new MenuItem("删除连接");
        deleteItem.setOnAction(t -> this.deleteConnection());
        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(openItem, closeItem, propertyItem, deleteItem);
    }

    private void openConnection() {
        ObservableList children = getChildren();
        if (children.isEmpty()) {
            for (int i = 0; i < 16; i++) {
                children.add(new RedisDatabaseItem(new RedisDatabaseProperty(i)));
            }
            setExpanded(true);
        } else {
            setExpanded(true);
        }
    }

    private void closeConnection() {
        ObservableList children = getChildren();
        children.clear();
        setExpanded(false);
    }

    private void connectionProperty() {
        //TODO

    }

    private void deleteConnection() {
        TreeItem parent = getParent();
        parent.getChildren().remove(this);
    }

    @Override
    public ContextMenu getContextMenu() {
        return this.contextMenu;
    }
}
