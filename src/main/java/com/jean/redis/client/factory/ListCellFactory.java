package com.jean.redis.client.factory;


import com.jean.redis.client.Service.DelService;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jinshubao
 * @date 2016/11/25
 */
@Component
public class ListCellFactory implements Callback<ListView<String>, ListCell<String>> {

    @Autowired
    DelService delService;


    @Override
    public ListCell<String> call(ListView<String> param) {
        return new ListCell<String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                    setContextMenu(null);
                } else {
                    setText(item == null ? "" : item);
                    setContextMenu(getMenu(param));
                }
            }
        };
    }

    private ContextMenu getMenu(ListView<String> param) {
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
