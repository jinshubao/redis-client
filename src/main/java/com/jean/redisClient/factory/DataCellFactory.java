package com.jean.redisClient.factory;


import com.jean.redisClient.Service.DelService;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinshubao on 2016/11/25.
 */
@Component
public class DataCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    @Autowired
    DelService delService;


    @Override
    public TableCell<S, T> call(TableColumn<S, T> p) {
        return new TableCell<S, T>() {
            @Override
            public void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item == null ? "" : item.toString());
                }
                getTableRow().setContextMenu(getMenu(item, (S) getTableRow().getItem()));
            }
        };
    }

    private ContextMenu getMenu(T item, S data) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem copy = new MenuItem("复制");
        copy.setOnAction(event -> {
            Map<DataFormat, Object> content = new HashMap<>();
            content.put(DataFormat.PLAIN_TEXT, item);
            Clipboard.getSystemClipboard().setContent(content);
        });
        MenuItem del = new MenuItem("删除");
        del.setOnAction(event -> {
            delService.clearParams();
            delService.addParams("item", data);
            delService.restart();
        });
        contextMenu.getItems().addAll(copy, del);
        return contextMenu;
    }
}
