package com.jean.redis.client.factory;

import com.jean.redis.client.view.handler.IRedisValueActionEventHandler;
import com.jean.redis.client.model.RedisValue;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;


public class RedisValueTableRowFactory implements Callback<TableView<RedisValue>, TableRow<RedisValue>> {

    private final IRedisValueActionEventHandler handler;

    public RedisValueTableRowFactory(IRedisValueActionEventHandler handler) {
        this.handler = handler;
    }

    @Override
    public TableRow<RedisValue> call(TableView<RedisValue> param) {
        return new RedisKeyTableRow(handler);
    }

    private static class RedisKeyTableRow extends TableRow<RedisValue> {

        private final ChangeListener<Boolean> changeListener;
        private final WeakChangeListener<Boolean> weakChangeListener;

        private final EventHandler<MouseEvent> mouseEventEventHandler;
        private final WeakEventHandler<MouseEvent> WeakMouseEventHandler;


        private RedisKeyTableRow(IRedisValueActionEventHandler handler) {

            this.changeListener = (observable, oldValue, newValue) -> {
                if (newValue) {
                    handler.onSelected(RedisKeyTableRow.this);
                }
            };
            this.weakChangeListener = new WeakChangeListener<>(this.changeListener);
            selectedProperty().addListener(this.weakChangeListener);

            this.mouseEventEventHandler = event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (event.getClickCount() == 1) {
                        handler.onClick(RedisKeyTableRow.this);
                    } else if (event.getClickCount() == 2) {
                        handler.onDoubleClick(RedisKeyTableRow.this);
                    }
                }
            };
            this.WeakMouseEventHandler = new WeakEventHandler<>(this.mouseEventEventHandler);
            setOnMouseClicked(this.WeakMouseEventHandler);
        }
    }
}