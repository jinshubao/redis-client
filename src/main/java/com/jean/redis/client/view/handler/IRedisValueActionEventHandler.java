package com.jean.redis.client.view.handler;

import com.jean.redis.client.model.RedisValue;
import javafx.scene.control.TableRow;

/**
 * Redis value 列表 事件处理器
 *
 * @author jinshubao
 */
public interface IRedisValueActionEventHandler extends IMouseEventHandler<TableRow<RedisValue>> {

    /**
     * 复制
     *
     * @param tableRow tableRow
     */
    void copy(TableRow<RedisValue> tableRow);

    /**
     * 删除
     *
     * @param tableRow tableRow
     */
    void delete(TableRow<RedisValue> tableRow);
}
