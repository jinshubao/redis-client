package com.jean.redis.client.factory.key;

import com.jean.redis.client.handler.RedisKeyActionEventHandler;
import com.jean.redis.client.model.RedisKey;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;


public class RedisKeyTableRowFactory implements Callback<TableView<RedisKey>, TableRow<RedisKey>> {

    private final RedisKeyActionEventHandler handler;

    public RedisKeyTableRowFactory(RedisKeyActionEventHandler handler) {
        this.handler = handler;
    }

    @Override
    public TableRow<RedisKey> call(TableView<RedisKey> param) {
        return new RedisKeyTableRow(handler);
    }

    private static class RedisKeyTableRow extends TableRow<RedisKey> {

        private final RedisKeyActionEventHandler handler;

        private RedisKeyTableRow(RedisKeyActionEventHandler handler) {
            this.handler = handler;
            setContextMenu(createTableRowContextMenu());
            setOnMouseClicked(event -> {
                RedisKey redisKey = getItem();
                this.handler.click(event, redisKey);
            });
        }

        @Override
        protected void updateItem(RedisKey item, boolean empty) {
            super.updateItem(item, empty);
        }

        private ContextMenu createTableRowContextMenu() {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem copy = new MenuItem("复制");
            copy.setOnAction(event -> {
                RedisKey redisKey = getItem();
                handler.copy(event, redisKey);
            });
            MenuItem del = new MenuItem("删除");
            del.setOnAction(event -> {
                RedisKey redisKey = getItem();
                handler.delete(event, redisKey);
            });
            contextMenu.getItems().addAll(copy, del);
            return contextMenu;
        }
    }
}
