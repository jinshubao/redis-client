package com.jean.redis.client.view.handler;

import com.jean.redis.client.view.RedisDatabaseItem;


/**
 * Redis 数据库节点事件处理器
 *
 * @author jinshubao
 */
public interface IRedisDatabaseItemActionEventHandler extends IMouseEventHandler<RedisDatabaseItem> {

    /**
     * 刷新
     *
     * @param treeItem treeItem
     */
    void refresh(RedisDatabaseItem treeItem);

    /**
     * 清空
     *
     * @param treeItem treeItem
     */
    void flush(RedisDatabaseItem treeItem);


}
