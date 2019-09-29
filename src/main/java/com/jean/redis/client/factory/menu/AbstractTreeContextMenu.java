package com.jean.redis.client.factory.menu;

import com.jean.redis.client.entry.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

public abstract class AbstractTreeContextMenu {

    public abstract ContextMenu createContextMenu(TreeItem treeItem, Node node);
}
