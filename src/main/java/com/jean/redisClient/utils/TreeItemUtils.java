package com.jean.redisClient.utils;

import com.jean.redisClient.model.DbModel;
import com.jean.redisClient.model.HostModel;
import com.jean.redisClient.model.NodeModel;
import javafx.scene.control.TreeItem;

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

}
