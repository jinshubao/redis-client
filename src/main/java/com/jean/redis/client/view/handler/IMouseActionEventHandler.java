package com.jean.redis.client.view.handler;

public interface IMouseActionEventHandler<T> {


    /**
     * 单击
     *
     * @param t 节点
     */
    void onClick(T t);

    /**
     * 双击
     *
     * @param t 节点
     */
    void onDoubleClick(T t);

    /**
     * 选择
     *
     * @param t 节点
     */
    void onSelected(T t);

}
