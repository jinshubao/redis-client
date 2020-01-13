package com.jean.redis.client.factory;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class TableViewIndexColumnCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    @Override
    public TableCell<S, T> call(TableColumn<S, T> param) {
        return new TableCell<S, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
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
