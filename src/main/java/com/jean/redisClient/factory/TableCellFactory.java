package com.jean.redisClient.factory;


import com.jean.redisClient.Service.DelService;
import com.jean.redisClient.model.ListModel;
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
public class TableCellFactory<T> implements Callback<TableColumn<ListModel, T>, TableCell<ListModel, T>> {

    @Autowired
    private DelService delService;

    @Override
    public TableCell<ListModel, T> call(TableColumn<ListModel, T> p) {
        return new TableCell<ListModel, T>() {
            @Override
            public void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item == null ? "" : item.toString());
                }
                getTableRow().setContextMenu(getMenu(item, (ListModel) getTableRow().getItem()));
            }
        };
    }

    private ContextMenu getMenu(T item, ListModel data) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem copy = new MenuItem("复制");
        copy.setOnAction(event -> {
            Map<DataFormat, Object> content = new HashMap<>();
            content.put(DataFormat.PLAIN_TEXT, item);
            Clipboard.getSystemClipboard().setContent(content);
        });
        MenuItem del = new MenuItem("删除");
        del.setOnAction(event -> {
            delService.addParams("item", data);
            delService.restart();
        });
        contextMenu.getItems().addAll(copy, del);
        return contextMenu;
    }
}
