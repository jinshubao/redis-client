package com.jean.redis.client.factory;


import com.jean.redis.client.constant.CommonConstant;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinshubao
 * @date 2016/11/25
 */
public class RedisKeyTableKeyColumnCellFactory<S> implements Callback<TableColumn<S, byte[]>, TableCell<S, byte[]>> {

    private static Logger logger = LoggerFactory.getLogger(RedisKeyTableKeyColumnCellFactory.class);

    @Override
    public TableCell<S, byte[]> call(TableColumn<S, byte[]> p) {
        return new KeyTableCell<>();
    }

    private static class KeyTableCell<S> extends TableCell<S, byte[]> {

        @Override
        protected void updateItem(byte[] item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(new String(item, CommonConstant.CHARSET_UTF8));
            }
        }
    }
}
