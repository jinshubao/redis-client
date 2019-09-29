package com.jean.redis.client.factory;


import com.jean.redis.client.Service.DelService;
import com.jean.redis.client.model.ListItem;
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
 * @author jinshubao
 * @date 2016/11/25
 */
@Component
public class TableCellFactory<T> implements Callback<TableColumn<ListItem, T>, TableCell<ListItem, T>> {

    private final DelService delService;

    @Autowired
    public TableCellFactory(DelService delService) {
        this.delService = delService;
    }

    @Override
    public TableCell<ListItem, T> call(TableColumn<ListItem, T> p) {

        return new TableCell<ListItem, T>() {
            @Override
            public void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    getTableRow().setContextMenu(null);
                } else {
                    String key = item.toString();
                    setText(key);
                    getTableRow().setContextMenu(createContextMenu(item, (ListItem) getTableRow().getItem()));
                }
            }
        };
    }

    private ContextMenu createContextMenu(T item, ListItem data) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem copy = new MenuItem("复制");
        copy.setOnAction(event -> {
            Map<DataFormat, Object> content = new HashMap<>();
            content.put(DataFormat.PLAIN_TEXT, item);
            Clipboard.getSystemClipboard().setContent(content);
        });
        MenuItem del = new MenuItem("删除");
        del.setOnAction(event -> delService.restart(data.getConfig(), data.getKey()));

        MenuItem setTtl = new MenuItem("设置超时时间");
        setTtl.setOnAction(event -> {

        });

        contextMenu.getItems().addAll(copy, del);
        return contextMenu;
    }
}
