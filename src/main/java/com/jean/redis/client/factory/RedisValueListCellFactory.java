package com.jean.redis.client.factory;


import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
public class RedisValueListCellFactory implements Callback<ListView<byte[]>, ListCell<byte[]>> {

    @Override
    public ListCell<byte[]> call(ListView<byte[]> param) {
        return new RedisValueListCell();
    }

    private static class RedisValueListCell extends ListCell<byte[]> {


        public RedisValueListCell() {
            setContextMenu(createContextMenu());
        }

        @Override
        public void updateItem(byte[] item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                setText(new String(item));
            }
        }

        private ContextMenu createContextMenu() {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem copy = new MenuItem("复制");
            copy.setOnAction(event -> {
                Map<DataFormat, Object> content = new HashMap<>();
                content.put(DataFormat.PLAIN_TEXT, new String(getItem()));
                Clipboard.getSystemClipboard().setContent(content);
            });
            contextMenu.getItems().add(copy);
            return contextMenu;
        }

    }


}
