package com.jean.redis.client.utils;

import com.jean.redis.client.entry.Node;
import com.jean.redis.client.model.DBNode;
import com.jean.redis.client.model.DirNode;
import com.jean.redis.client.model.HostNode;
import com.jean.redis.client.model.ListItem;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.List;

/**
 * @author jinshubao
 * @date 2016/11/28
 */
public class TreeItemUtils {

    public static TreeItem<Node> createHostNode(HostNode hostNode) {
        TreeItem<Node> treeItem = new TreeItem<>(hostNode);
        for (int i = 0; i < 16; i++) {
            treeItem.getChildren().add(new TreeItem<>(new DBNode(hostNode.getHostName(), hostNode.getPort(), hostNode.getAuth(), i)));
        }
        return treeItem;
    }


    public static void generateDir(TreeItem<Node> dbTreeItem, List<ListItem> models) {
        dbTreeItem.getChildren().clear();
        Platform.runLater(() -> models.stream().filter(model -> model != null && (model.getKey().contains(":"))).forEach(model -> {
            String fullKey = model.getKey();
            Node itemValue = dbTreeItem.getValue();
            if (itemValue instanceof DirNode) {
                DirNode dirModel = (DirNode) itemValue;
                fullKey = fullKey.replaceFirst(dirModel.getFullPath(), "");
            }
            String[] keys = fullKey.split(":");
            if (!fullKey.contains(":")) {
                return;
            }
            String key = keys[0];
            String fullName = key + ":";
            if (itemValue instanceof DirNode) {
                DirNode dirModel = (DirNode) itemValue;
                fullName = dirModel.getFullPath() + fullName;
            }

            boolean exist = false;
            ObservableList<TreeItem<Node>> children = dbTreeItem.getChildren();
            for (TreeItem<Node> treeItem : children) {
                DirNode value = (DirNode) treeItem.getValue();
                if (key.equals(value.getDirName())) {
                    fullName = value.getFullPath() + fullName;
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                DBNode dbNode = (DBNode) itemValue;
                DirNode dirNode = new DirNode(dbNode.getHostName(), dbNode.getPort(), dbNode.getAuth(), dbNode.getDbIndex(), key, fullName);
                children.add(new TreeItem<>(dirNode));
            }
        }));
    }
}
