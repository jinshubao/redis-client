package com.jean.redis.client.handler;

import com.jean.redis.client.model.RedisKey;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;


public interface RedisKeyActionEventHandler {

    /**
     * 点击
     *
     * @param mouseEvent event
     * @param redisKey   key
     */
    void click(MouseEvent mouseEvent, RedisKey redisKey);

    /**
     * 复制
     *
     * @param actionEvent event
     * @param redisKey    key
     */
    void copy(ActionEvent actionEvent, RedisKey redisKey);

    /**
     * 删除
     *
     * @param actionEvent event
     * @param redisKey    key
     */
    void delete(ActionEvent actionEvent, RedisKey redisKey);
}
