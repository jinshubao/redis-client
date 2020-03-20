package com.jean.redis.client.view.handler.impl;

import com.jean.redis.client.model.RedisValue;
import com.jean.redis.client.util.NodeUtils;
import com.jean.redis.client.util.StringUtils;
import com.jean.redis.client.view.handler.IRedisValueActionEventHandler;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * @author jinshubao
 */
public class RedisValueActionEventHandlerImpl implements IRedisValueActionEventHandler {

    private final TextField keyTextFiled;
    private final TextArea valueTextArea;

    public RedisValueActionEventHandlerImpl(Node root) {
        this.keyTextFiled = NodeUtils.lookup(root, "#keyTextFiled");
        this.valueTextArea = NodeUtils.lookup(root, "#valueTextArea");
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
