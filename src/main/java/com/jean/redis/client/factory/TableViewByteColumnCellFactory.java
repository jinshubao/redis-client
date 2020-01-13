package com.jean.redis.client.factory;


import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.nio.charset.Charset;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
public class TableViewByteColumnCellFactory<T> implements Callback<TableColumn<T, byte[]>, TableCell<?, byte[]>> {

    private final Charset charset;

    public TableViewByteColumnCellFactory(Charset charset) {
        this.charset = charset;
    }

    @Override
    public TableCell<T, byte[]> call(TableColumn<T, byte[]> p) {
        return new KeyTableCell<>(charset);
    }

    private static class KeyTableCell<T> extends TableCell<T, byte[]> {
        private final Charset charset;

        private KeyTableCell(Charset charset) {
            this.charset = charset;
        }

        @Override
        protected void updateItem(byte[] item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(new String(item, charset));
            }
        }
    }
}
