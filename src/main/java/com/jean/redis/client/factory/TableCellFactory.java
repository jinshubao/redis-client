package com.jean.redis.client.factory;


import com.jean.redis.client.Service.DelService;
import com.jean.redis.client.model.ListItem;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
@Component
public class TableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    private static Logger logger = LoggerFactory.getLogger(TableCellFactory.class);

    private final DelService delService;

    @Autowired
    public TableCellFactory(DelService delService) {
        this.delService = delService;
    }

    @Override
    public TableCell<S, T> call(TableColumn<S, T> p) {

        return new TableCell<S, T>() {
            @Override
            public void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                TableView<S> tableView = getTableView();
                Object rowValue = getTableRow().getItem();
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    getTableRow().setContextMenu(null);
                } else {
                    String key = item.toString();
                    setText(key);
                    getTableRow().setContextMenu(createTableRowContextMenu(tableView, rowValue, item));
                }
            }
        };
    }

    private ContextMenu createTableRowContextMenu(TableView<S> tableView, Object rowValue, T cellValue) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem copy = new MenuItem("复制");
        copy.setOnAction(event -> {
            Map<DataFormat, Object> content = new HashMap<>();
            content.put(DataFormat.PLAIN_TEXT, cellValue);
            Clipboard.getSystemClipboard().setContent(content);
        });
        MenuItem del = new MenuItem("删除");
        del.setOnAction(event -> {
            tableView.getItems().remove(rowValue);
            ListItem listItem = (ListItem) rowValue;
            delService.restart((listItem).getConfig(), listItem.getKey());
        });

        MenuItem setTtl = new MenuItem("设置超时时间");
        setTtl.setOnAction(event -> {

        });

        contextMenu.getItems().addAll(copy, del, setTtl);
        return contextMenu;
    }
}
