package com.jean.redis.client.factory.menu;

import com.jean.redis.client.dialog.CreateRedisServerDialog;
import com.jean.redis.client.entry.Node;
import com.jean.redis.client.model.ConfigProperty;
import com.jean.redis.client.model.HostNode;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

public class RootContextMenu extends AbstractTreeContextMenu {

    @Override
    public ContextMenu createContextMenu(TreeItem treeItem, Node node) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem add = new MenuItem("新建连接");
        contextMenu.getItems().add(add);
        add.setOnAction(t -> {
            createDialog().showAndWait().ifPresent(property -> {
                ConfigProperty configProperty = new ConfigProperty(property.getHost(), property.getPort(), property.getPassword(), 0);
                treeItem.getChildren().add(new TreeItem<>(new HostNode(configProperty)));
            });
        });
        return contextMenu;
    }

    private CreateRedisServerDialog createDialog() {
        return new CreateRedisServerDialog(null);
    }
}
