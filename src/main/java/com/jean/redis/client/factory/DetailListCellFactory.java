package com.jean.redis.client.factory;


import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
public class DetailListCellFactory<V> implements Callback<ListView<V>, ListCell<V>> {


    @Override
    public ListCell<V> call(ListView<V> param) {
        return new ListCell<V>() {
            @Override
            public void updateItem(V item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                    setContextMenu(null);
                    return;
                }
                if (Objects.isNull(item)) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setContextMenu(createContextMenu(param));
            }
        };
    }

    private ContextMenu createContextMenu(ListView<V> param) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem copy = new MenuItem("复制");
        copy.setOnAction(event -> {
            Map<DataFormat, Object> content = new HashMap<>();
            List items = param.getSelectionModel().getSelectedItems();
            StringBuilder text = new StringBuilder();
            int size = items.size();
            for (int i = 0; i < size; i++) {
                text.append(items.get(i));
                if (i < size - 1) {
                    text.append(", ");
                }
            }
            content.put(DataFormat.PLAIN_TEXT, text.toString());
            Clipboard.getSystemClipboard().setContent(content);
        });
        contextMenu.getItems().add(copy);
        return contextMenu;
    }
}
