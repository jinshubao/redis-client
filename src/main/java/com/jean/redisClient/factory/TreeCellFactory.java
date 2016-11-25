package com.jean.redisClient.factory;


import com.jean.redisClient.Service.DelService;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jinshubao on 2016/11/25.
 */
@Component
public class TreeCellFactory<T> implements Callback<TreeItem<T>, TreeCell<T>> {

    @Autowired
    DelService delService;

    @Override
    public TreeCell<T> call(TreeItem<T> p) {
        return new TreeCell<T>() {
            @Override
            public void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

            }
        };
    }

}
