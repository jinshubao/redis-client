package com.jean.redis.client.factory;


import com.jean.redis.client.model.RedisKey;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.nio.charset.Charset;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
public class RedisKeyTableKeyColumnCellFactory implements Callback<TableColumn<RedisKey, byte[]>, TableCell<RedisKey, byte[]>> {

    private final Charset charset;

    public RedisKeyTableKeyColumnCellFactory(Charset charset) {
        this.charset = charset;
    }

    @Override
    public TableCell<RedisKey, byte[]> call(TableColumn<RedisKey, byte[]> p) {
        return new KeyTableCell(charset);
    }

    private static class KeyTableCell extends TableCell<RedisKey, byte[]> {
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
