package com.jean.redisClient.factory;


import com.jean.redisClient.model.HostModel;
import com.jean.redisClient.model.NodeModel;
import com.jean.redisClient.model.RootModel;
import com.jean.redisClient.utils.Utils;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.springframework.stereotype.Component;

/**
 * Created by jinshubao on 2016/11/25.
 */
@Component
public class TreeCellFactory implements Callback<TreeView<NodeModel>, TreeCell<NodeModel>> {

    @Override
    public TreeCell<NodeModel> call(TreeView<NodeModel> param) {

        return new TreeCell<NodeModel>() {
            @Override
            protected void updateItem(NodeModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    TreeItem<NodeModel> treeItem = getTreeItem();
                    ContextMenu contextMenu = new ContextMenu();
                    if (item instanceof RootModel) {
                        MenuItem add = new MenuItem("新增链接");
                        contextMenu.getItems().add(add);
                        add.setOnAction(t -> {
                            HostModel hostModel = new HostModel("new host", 6379);
                            TreeItem<NodeModel> host = new TreeItem<>(hostModel);
                            host.getChildren().addAll(Utils.getDbItems(hostModel));
                            treeItem.getChildren().add(host);
                        });
                        setContextMenu(contextMenu);
                    } else if (item instanceof HostModel) {
                        MenuItem del = new MenuItem("删除");
                        contextMenu.getItems().add(del);
                        del.setOnAction(t -> treeItem.getParent().getChildren().remove(treeItem));
                        setContextMenu(contextMenu);
                    }
                    setText(item == null ? "" : item.toString());
                }
            }
        };
    }
}
