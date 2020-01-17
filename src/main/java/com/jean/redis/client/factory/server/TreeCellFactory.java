package com.jean.redis.client.factory.server;


import com.jean.redis.client.item.Menuable;
import com.jean.redis.client.item.MouseClickable;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

/**
 * Created by jinshubao on 2016/11/25.
 */
public class TreeCellFactory implements Callback<TreeView<Object>, TreeCell<Object>> {

    @Override
    public TreeCell<Object> call(TreeView<Object> param) {
        return new RedisServerTreeCell();
    }

    static class RedisServerTreeCell extends TreeCell<Object> {

        RedisServerTreeCell() {
            setOnMouseClicked(event -> {
                TreeItem<Object> treeItem = getTreeItem();
                if (treeItem instanceof MouseClickable) {
                    EventHandler<MouseEvent> eventHandler = ((MouseClickable) treeItem).getClickEventHandler();
                    eventHandler.handle(event);
                }
            });
        }

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                setContextMenu(null);
            } else {
                setText(item.toString());
                TreeItem<Object> treeItem = getTreeItem();
                if (treeItem instanceof Menuable) {
                    Menuable treeContextMenu = (Menuable) treeItem;
                    ContextMenu contextMenu = treeContextMenu.getContextMenu();
                    this.setContextMenu(contextMenu);
                }
            }
        }
    }
}
