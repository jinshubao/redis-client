package com.jean.redis.client.factory;


import com.jean.redis.client.item.ContextMenuable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by jinshubao on 2016/11/25.
 */
@Component
public class RedisTreeCellFactory implements Callback<TreeView<Object>, TreeCell<Object>> {

    private static Logger logger = LoggerFactory.getLogger(RedisTreeCellFactory.class);

    @Override
    public TreeCell<Object> call(TreeView<Object> param) {
        return new RedisServerTreeCell();
    }

    static class RedisServerTreeCell extends TreeCell<Object> {

        public RedisServerTreeCell() {

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
                if (treeItem instanceof ContextMenuable) {
                    ContextMenuable treeContextMenu = (ContextMenuable) treeItem;
                    ContextMenu contextMenu = treeContextMenu.getContextMenu();
                    this.setContextMenu(contextMenu);
                }
            }
        }
    }
}
