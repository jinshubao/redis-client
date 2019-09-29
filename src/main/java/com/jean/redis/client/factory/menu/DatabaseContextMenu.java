package com.jean.redis.client.factory.menu;

import com.jean.redis.client.entry.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

public class DatabaseContextMenu extends AbstractTreeContextMenu {

    @Override
    public ContextMenu createContextMenu(TreeItem treeItem, Node item) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem refresh = new MenuItem("刷新");
        MenuItem flush = new MenuItem("清空");
        contextMenu.getItems().addAll(refresh, flush);
        refresh.setOnAction(t -> {
        });
        flush.setOnAction(event -> {

        });
        return contextMenu;
    }
}
