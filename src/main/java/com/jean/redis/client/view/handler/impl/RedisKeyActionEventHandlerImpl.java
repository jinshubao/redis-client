package com.jean.redis.client.view.handler.impl;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.mange.TaskManger;
import com.jean.redis.client.model.RedisKey;
import com.jean.redis.client.model.RedisValue;
import com.jean.redis.client.model.RedisValueWrapper;
import com.jean.redis.client.task.RedisValueTask;
import com.jean.redis.client.util.StringUtils;
import com.jean.redis.client.util.ViewUtils;
import com.jean.redis.client.view.ProgressIndicatorPlaceholder;
import com.jean.redis.client.view.handler.IRedisKeyActionEventHandler;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.scene.control.*;

/**
 * @author jinshubao
 */
public class RedisKeyActionEventHandlerImpl implements IRedisKeyActionEventHandler {

    private final TableView<RedisKey> keyTableView;
    private final TableView<RedisValue> valueTableView;
    private final TableColumn<RedisValue, Integer> valueNoColumn;
    private final TableColumn<RedisValue, byte[]> valueKeyColumn;
    private final TableColumn<RedisValue, byte[]> valueColumn;
    private final TableColumn<RedisValue, Number> valueScoreColumn;
    private final TextField keyTextFiled;
    private final TextArea valueTextArea;
    private final EventHandler<WorkerStateEvent> valueTaskWorkerStateEventHandler;

    public RedisKeyActionEventHandlerImpl() {
        this.keyTableView = ViewUtils.getInstance().getKeyTableView();
        this.valueTableView = ViewUtils.getInstance().getValueTableView();

        this.valueNoColumn = ViewUtils.getInstance().getValueNoColumn();
        this.valueKeyColumn = ViewUtils.getInstance().getValueKeyColumn();
        this.valueColumn = ViewUtils.getInstance().getValueColumn();
        this.valueScoreColumn = ViewUtils.getInstance().getValueScoreColumn();

        this.keyTextFiled = ViewUtils.getInstance().getKeyTextFiled();
        this.valueTextArea = ViewUtils.getInstance().getValueTextArea();

        SplitPane valueSplitPane = ViewUtils.getInstance().getValueSplitPane();

        //redis getValue task success event handler
        this.valueTaskWorkerStateEventHandler = event -> {

            ProgressIndicatorPlaceholder valueProgressIndicator = (ProgressIndicatorPlaceholder) valueTableView.getPlaceholder();

            if (event.getEventType() == WorkerStateEvent.WORKER_STATE_SCHEDULED) {
                valueTableView.getItems().clear();
                valueProgressIndicator.indicatorProgressProperty().unbind();
                valueProgressIndicator.indicatorProgressProperty().bind(event.getSource().progressProperty());
                valueProgressIndicator.indicatorVisibleProperty().unbind();
                valueProgressIndicator.indicatorVisibleProperty().bind(event.getSource().runningProperty());
            } else if (event.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
                RedisValueWrapper value = (RedisValueWrapper) event.getSource().getValue();
                if (value != null) {
                    if (CommonConstant.KeyType.STRING.equalsIgnoreCase(value.getType())) {
                        valueSplitPane.getItems().remove(valueTableView);
                    } else {
                        if (!valueSplitPane.getItems().contains(valueTableView)) {
                            valueSplitPane.getItems().add(0, valueTableView);
                        }
                    }
                    valueKeyColumn.setVisible(CommonConstant.KeyType.HASH.equalsIgnoreCase(value.getType()));
                    valueScoreColumn.setVisible(CommonConstant.KeyType.ZSET.equalsIgnoreCase(value.getType()));
                    if (CommonConstant.KeyType.STRING.equalsIgnoreCase(value.getType())) {
                        keyTextFiled.setText(StringUtils.byteArrayToString(value.getKey()));
                        valueTextArea.setText(StringUtils.byteArrayToString(value.getValues().get(0).getValue()));
                    } else {
                        keyTextFiled.setText(null);
                        valueTextArea.setText(null);
                        valueTableView.getItems().addAll(value.getValues());
                    }
                }
            }
        };
    }

    @Override
    public void onDoubleClick(TableRow<RedisKey> tableRow) {
        RedisKey redisKey = tableRow.getItem();
        RedisValueTask task = new RedisValueTask(redisKey.getServer(), redisKey.getDatabase(), redisKey.getKey());
        TaskManger.getInstance().execute(task, new WeakEventHandler<>(this.valueTaskWorkerStateEventHandler));
    }

    @Override
    public void copy(TableRow<RedisKey> tableRow) {

    }

    @Override
    public void delete(TableRow<RedisKey> tableRow) {
        keyTableView.getItems().remove(tableRow.getItem());
    }


    @Override
    public void onSelected(TableRow<RedisKey> tableRow) {
        RedisKey redisKey = tableRow.getItem();
        RedisValueTask task = new RedisValueTask(redisKey.getServer(), redisKey.getDatabase(), redisKey.getKey());
        TaskManger.getInstance().execute(task, new WeakEventHandler<>(this.valueTaskWorkerStateEventHandler));
    }
}
