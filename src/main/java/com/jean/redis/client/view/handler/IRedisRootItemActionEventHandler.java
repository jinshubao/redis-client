package com.jean.redis.client.view.handler;

import com.jean.redis.client.view.RedisRootItem;

/**
 * 根节点事件处理器
 */
public interface IRedisRootItemActionEventHandler extends IMouseActionEventHandler<RedisRootItem> {

    /**
     * 新增服务器
     *
     * @param redisRootItem redisRootItem
     */
    void create(RedisRootItem redisRootItem);

}
