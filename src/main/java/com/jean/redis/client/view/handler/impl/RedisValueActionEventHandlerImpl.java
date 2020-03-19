package com.jean.redis.client.view.handler.impl;

import com.jean.redis.client.view.handler.BaseMouseEventHandler;
import com.jean.redis.client.view.handler.IRedisValueActionEventHandler;
import com.jean.redis.client.model.RedisValue;
import com.jean.redis.client.util.StringUtils;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class RedisValueActionEventHandlerImpl extends BaseMouseEventHandler<TableRow<RedisValue>> implements IRedisValueActionEventHandler {

    private TextField keyTextFiled;
    private TextArea valueTextArea;

    public RedisValueActionEventHandlerImpl(Node root) {
        super(root);
        SplitPane splitPane = (SplitPane) root.lookup("#splitPane");
        SplitPane valueSplitPane = (SplitPane) splitPane.getItems().stream().filter(item -> "valueSplitPane".equals(item.getId())).findFirst().orElseThrow(() -> new RuntimeException("id='valueSplitPane' not fund"));
        GridPane valueGridPane = (GridPane) valueSplitPane.getItems().stream().filter(item -> "valueGridPane".equals(item.getId())).findFirst().orElseThrow(() -> new RuntimeException("id='valueGridPane' not fund"));
        this.keyTextFiled = (TextField) valueGridPane.lookup("#keyTextFiled");
        this.valueTextArea = (TextArea) valueGridPane.lookup("#valueTextArea");
    }

    @Override
    public void copy(TableRow<RedisValue> tableRow) {

    }

    @Override
    public void delete(TableRow<RedisValue> tableRow) {

    }

    @Override
    public void onSelected(TableRow<RedisValue> tableRow) {
        RedisValue redisValue = tableRow.getItem();
        this.keyTextFiled.setText(StringUtils.byteArrayToString(redisValue.getKey()));
        this.valueTextArea.setText(StringUtils.byteArrayToString(redisValue.getValue()));
    }
}
