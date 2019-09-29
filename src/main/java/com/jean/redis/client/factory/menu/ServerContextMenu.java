package com.jean.redis.client.factory.menu;

import com.jean.redis.client.entry.Node;
import com.jean.redis.client.model.ConfigProperty;
import com.jean.redis.client.model.DBNode;
import com.jean.redis.client.model.HostNode;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

public class ServerContextMenu extends AbstractTreeContextMenu {

    @Override
    public ContextMenu createContextMenu(TreeItem treeItem, Node item) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem connect = new MenuItem("打开连接");
        MenuItem close = new MenuItem("关闭连接");
        MenuItem del = new MenuItem("删除连接");
        MenuItem properties = new MenuItem("连接属性");
        contextMenu.getItems().addAll(connect, close, del, properties);

        connect.setOnAction(t -> {
            if (treeItem.getChildren().isEmpty()) {
                ConfigProperty config = ((HostNode) item).getConfig();
                for (int i = 0; i < 16; i++) {
                    treeItem.getChildren().add(new TreeItem<>(new DBNode(new ConfigProperty(config, i))));
                }
                treeItem.setExpanded(true);
            }
        });
        close.setOnAction(t -> {
            treeItem.getChildren().clear();
            treeItem.setExpanded(false);
        });
        del.setOnAction(t -> {
            treeItem.getParent().getChildren().remove(treeItem);
        });
        properties.setOnAction(event -> {

        });
        return contextMenu;
    }
}
