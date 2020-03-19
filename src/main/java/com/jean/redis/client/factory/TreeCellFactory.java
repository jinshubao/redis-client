package com.jean.redis.client.factory;


import com.jean.redis.client.view.action.IContextMenu;
import com.jean.redis.client.view.action.IMouseAction;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
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
                if (treeItem instanceof IMouseAction) {
                    ((IMouseAction) treeItem).click(event);
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
                if (treeItem instanceof IContextMenu) {
                    IContextMenu treeContextMenu = (IContextMenu) treeItem;
                    ContextMenu contextMenu = treeContextMenu.getContextMenu();
                    this.setContextMenu(contextMenu);
                }else{
                    setContextMenu(null);
                }
            }
        }
    }
}
