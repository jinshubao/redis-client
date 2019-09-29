package com.jean.redis.client.factory;


import com.jean.redis.client.entry.Node;
import com.jean.redis.client.entry.NodeType;
import com.jean.redis.client.factory.menu.AbstractTreeContextMenu;
import com.jean.redis.client.factory.menu.DatabaseContextMenu;
import com.jean.redis.client.factory.menu.RootContextMenu;
import com.jean.redis.client.factory.menu.ServerContextMenu;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by jinshubao on 2016/11/25.
 */
public class TreeCellFactory implements Callback<TreeView<Node>, TreeCell<Node>> {

    @Override
    public TreeCell<Node> call(TreeView<Node> param) {

        return new TreeCell<Node>() {
            @Override
            protected void updateItem(Node item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setContextMenu(null);
                } else {
                    setText(item.toString());
                    TreeItem<Node> treeItem = getTreeItem();
                    ContextMenu contextMenu = TreeContextMenuManager.createContextMenu(treeItem, item);
                    if (contextMenu != null && !contextMenu.getItems().isEmpty()) {
                        setContextMenu(contextMenu);
                    }
                }
            }
        };
    }


    static class TreeContextMenuManager {

        private static Map<NodeType, AbstractTreeContextMenu> contextMenuHashMap = new HashMap<>();

        static {
            contextMenuHashMap.put(NodeType.ROOT, new RootContextMenu());
            contextMenuHashMap.put(NodeType.HOST, new ServerContextMenu());
            contextMenuHashMap.put(NodeType.DB, new DatabaseContextMenu());
        }

        public static ContextMenu createContextMenu(TreeItem treeItem, Node node) {
            if (Objects.isNull(node)) {
                return null;
            }
            if (!contextMenuHashMap.containsKey(node.getNodeType())) {
                return null;
            }
            return contextMenuHashMap.get(node.getNodeType()).createContextMenu(treeItem, node);
        }

    }
}
