package com.jean.redis.client.view.handler.impl;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.view.ProgressIndicatorPlaceholder;
import com.jean.redis.client.view.handler.BaseMouseEventHandler;
import com.jean.redis.client.view.handler.IRedisKeyActionEventHandler;
import com.jean.redis.client.mange.TaskManger;
import com.jean.redis.client.model.RedisKey;
import com.jean.redis.client.model.RedisValue;
import com.jean.redis.client.model.RedisValueWrapper;
import com.jean.redis.client.task.RedisValueTask;
import com.jean.redis.client.util.StringUtils;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class RedisKeyActionEventHandlerImpl extends BaseMouseEventHandler<TableRow<RedisKey>> implements IRedisKeyActionEventHandler {

    private TableView<RedisKey> keyTableView;
    private TableView<RedisValue> valueTableView;
    private TableColumn<RedisValue, Integer> valueNoColumn;
    private TableColumn<RedisValue, byte[]> valueKeyColumn;
    private TableColumn<RedisValue, byte[]> valueColumn;
    private TableColumn<RedisValue, Number> valueScoreColumn;
    private TextField keyTextFiled;
    private TextArea valueTextArea;

    private EventHandler<WorkerStateEvent> valueTaskWorkerStateEventHandler;


    public RedisKeyActionEventHandlerImpl(Node root) {
        super(root);

        SplitPane splitPane = (SplitPane) root.lookup("#splitPane");
        this.keyTableView = (TableView<RedisKey>) splitPane.getItems().stream().filter(item -> "keyTableView".equals(item.getId())).findFirst().orElseThrow(()-> new RuntimeException("id='keyTableView' not fund"));
        SplitPane  valueSplitPane = (SplitPane) splitPane.getItems().stream().filter(item -> "valueSplitPane".equals(item.getId())).findFirst().orElseThrow(()-> new RuntimeException("id='valueSplitPane' not fund"));
        this.valueTableView = (TableView<RedisValue>) valueSplitPane.getItems().stream().filter(item -> "valueTableView".equals(item.getId())).findFirst().orElseThrow(()-> new RuntimeException("id='valueTableView' not fund"));
        GridPane valueGridPane = (GridPane) valueSplitPane.getItems().stream().filter(item -> "valueGridPane".equals(item.getId())).findFirst().orElseThrow(()-> new RuntimeException("id='valueGridPane' not fund"));
        this.keyTextFiled = (TextField) valueGridPane.lookup("#keyTextFiled");
        this.valueTextArea = (TextArea) valueGridPane.lookup("#valueTextArea");

        this.valueNoColumn = (TableColumn<RedisValue, Integer>) this.valueTableView.getColumns().get(0);
        this.valueKeyColumn = (TableColumn<RedisValue, byte[]>) this.valueTableView.getColumns().get(1);
        this.valueColumn = (TableColumn<RedisValue, byte[]>) this.valueTableView.getColumns().get(2);
        this.valueScoreColumn = (TableColumn<RedisValue, Number>) this.valueTableView.getColumns().get(3);

        //redis getValue task success event handler
        valueTaskWorkerStateEventHandler = event -> {

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
        TaskManger.getInstance().execute(task, valueTaskWorkerStateEventHandler);
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
        TaskManger.getInstance().execute(task, valueTaskWorkerStateEventHandler);
    }
}
