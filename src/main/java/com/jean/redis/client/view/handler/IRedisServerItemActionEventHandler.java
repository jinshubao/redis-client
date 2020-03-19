package com.jean.redis.client.view.handler;

import com.jean.redis.client.view.RedisServerItem;

/**
 * 服务器信息节点事件处理器
 */
public interface IRedisServerItemActionEventHandler extends IMouseActionEventHandler<RedisServerItem> {

    /**
     * @param treeItem       treeItem
     */
    void open(RedisServerItem treeItem);

    /**
     * @param treeItem       treeItem
     */
    void close(RedisServerItem treeItem);

    /**
     * @param treeItem       treeItem
     */
    void delete(RedisServerItem treeItem);

    /**
     * @param treeItem       treeItem
     */
    void property(RedisServerItem treeItem);


}
