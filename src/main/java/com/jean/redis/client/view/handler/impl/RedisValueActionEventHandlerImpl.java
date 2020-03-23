package com.jean.redis.client.view.handler.impl;

import com.jean.redis.client.model.RedisValue;
import com.jean.redis.client.util.StringUtils;
import com.jean.redis.client.util.ViewUtils;
import com.jean.redis.client.view.handler.IRedisValueActionEventHandler;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * @author jinshubao
 */
public class RedisValueActionEventHandlerImpl implements IRedisValueActionEventHandler {

    private final TextField keyTextFiled;
    private final TextArea valueTextArea;

    public RedisValueActionEventHandlerImpl() {
        this.keyTextFiled = ViewUtils.getInstance().getKeyTextFiled();
        this.valueTextArea = ViewUtils.getInstance().getValueTextArea();
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
