package com.jean.redis.client.view.handler;

/**
 * 鼠标事件处理器
 *
 * @author jinshubao
 */
public interface IMouseEventHandler<T> {

    /**
     * 单击
     *
     * @param t 节点
     */
    default void onClick(T t) {
    }


    /**
     * 双击
     *
     * @param t 节点
     */
    default void onDoubleClick(T t) {
    }


    /**
     * 选择
     *
     * @param t 节点
     */
    default void onSelected(T t) {
    }


}
