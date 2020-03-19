package com.jean.redis.client.view.handler;

import com.jean.redis.client.view.RedisDatabaseItem;

public interface IRedisDatabaseItemActionEventHandler extends IMouseActionEventHandler<RedisDatabaseItem> {


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
