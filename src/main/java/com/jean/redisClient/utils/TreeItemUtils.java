package com.jean.redisClient.utils;

import com.jean.redisClient.model.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.List;

/**
 * Created by jinshubao on 2016/11/28.
 */
public class TreeItemUtils {

    public static TreeItem<NodeModel> createHostTreeItem(HostModel hostModel) {
        TreeItem<NodeModel> hostModelTreeItem = new TreeItem<>(hostModel);
        for (int i = 0; i < 16; i++) {
            hostModelTreeItem.getChildren().add(new TreeItem<>(new DbModel(hostModel, i)));
        }
        return hostModelTreeItem;
    }

    public static void generateDir(TreeItem<NodeModel> dbTreeItem, List<ListModel> models) {
        dbTreeItem.getChildren().clear();
        Platform.runLater(() -> models.stream().filter(model -> model != null && (model.getKey().contains(":"))).forEach(model -> {
            String fullKey = model.getKey();
            NodeModel itemValue = dbTreeItem.getValue();
            if (itemValue instanceof DirModel) {
                DirModel dirModel = (DirModel) itemValue;
                fullKey = fullKey.replaceFirst(dirModel.getFullPath(), "");
            }
            String[] keys = fullKey.split(":");
            if (!fullKey.contains(":")) {
                return;
            }
            String key = keys[0];
            String fullName = key + ":";
            if (itemValue instanceof DirModel) {
                DirModel dirModel = (DirModel) itemValue;
                fullName = dirModel.getFullPath() + fullName;
            }

            boolean exsit = false;
            ObservableList<TreeItem<NodeModel>> children = dbTreeItem.getChildren();
            for (TreeItem<NodeModel> treeItem : children) {
                DirModel value = (DirModel) treeItem.getValue();
                if (key.equals(value.getDirName())) {
                    fullName = value.getFullPath() + fullName;
                    exsit = true;
                    break;
                }
            }
            if (!exsit) {
                children.add(new TreeItem<>(new DirModel((DbModel) itemValue, key, fullName)));
            }
        }));
    }
}
