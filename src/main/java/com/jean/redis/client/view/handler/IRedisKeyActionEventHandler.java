package com.jean.redis.client.view.handler;

import com.jean.redis.client.model.RedisKey;
import javafx.scene.control.TableRow;

/**
 * redis key 列表 事件处理器
 */
public interface IRedisKeyActionEventHandler extends IMouseActionEventHandler<TableRow<RedisKey>> {

    /**
     * 复制
     *
     * @param tableRow tableRow
     */
    void copy(TableRow<RedisKey> tableRow);

    /**
     * 删除
     *
     * @param tableRow tableRow
     */
    void delete(TableRow<RedisKey> tableRow);
}
