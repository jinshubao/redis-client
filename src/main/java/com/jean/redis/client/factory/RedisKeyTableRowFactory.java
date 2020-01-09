package com.jean.redis.client.factory;

import com.jean.redis.client.model.RedisKey;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class RedisKeyTableRowFactory implements Callback<TableView<RedisKey>, TableRow<RedisKey>> {

    private static Logger logger = LoggerFactory.getLogger(RedisKeyTableRowFactory.class);

    @Override
    public TableRow<RedisKey> call(TableView<RedisKey> param) {
        return new RedisKeyTableRow();
    }

    private static class RedisKeyTableRow extends TableRow<RedisKey> {

        private RedisKeyTableRow() {
            setContextMenu(createTableRowContextMenu());
        }

        @Override
        protected void updateItem(RedisKey item, boolean empty) {
            super.updateItem(item, empty);
        }

        private ContextMenu createTableRowContextMenu() {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem copy = new MenuItem("复制");
            copy.setOnAction(event -> {
                Map<DataFormat, Object> content = new HashMap<>();
                content.put(DataFormat.PLAIN_TEXT, new String(getItem().getKey()));
                Clipboard.getSystemClipboard().setContent(content);
            });
            MenuItem del = new MenuItem("删除");
            del.setOnAction(event -> {
                getTableView().getItems().remove(getItem());
            });

            MenuItem setTtl = new MenuItem("设置超时时间");
            setTtl.setOnAction(event -> {
                RedisKey redisKey = getItem();
                long ttl = redisKey.getTtl();
                TextInputDialog dialog = new TextInputDialog(String.valueOf(ttl));
                dialog.setTitle("设置超时时间");
                dialog.setHeaderText("key：" + new String(redisKey.getKey()));
                dialog.setContentText("超时时间（s）：");
                dialog.showAndWait().ifPresent((value) -> {
                    if (!value.isEmpty()) {
                        long ttlValue = Long.valueOf(value);
                        logger.debug("ttlValue {}", ttlValue);
                    }
                });
            });

            contextMenu.getItems().addAll(copy, del, setTtl);
            return contextMenu;
        }
    }
}
