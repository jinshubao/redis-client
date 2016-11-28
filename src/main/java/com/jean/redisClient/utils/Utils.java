package com.jean.redisClient.utils;

import com.jean.redisClient.model.DbModel;
import com.jean.redisClient.model.HostModel;
import com.jean.redisClient.model.NodeModel;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinshubao on 2016/11/28.
 */
public class Utils {

    public static List<TreeItem<NodeModel>> getDbItems(HostModel hostModel) {
        List<TreeItem<NodeModel>> list = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            list.add(new TreeItem<>(new DbModel(hostModel, i)));
        }
        return list;
    }

}
