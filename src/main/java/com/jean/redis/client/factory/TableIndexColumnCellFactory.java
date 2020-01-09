package com.jean.redis.client.factory;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class TableIndexColumnCellFactory implements Callback<TableColumn<Object, Object>, TableCell<Object, Object>> {

    @Override
    public TableCell<Object, Object> call(TableColumn<Object, Object> param) {
        return new TableCell<Object, Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    setText(String.valueOf(getTableRow().getIndex() + 1));
                } else {
                    setText(null);
                }
            }
        };
    }
}
