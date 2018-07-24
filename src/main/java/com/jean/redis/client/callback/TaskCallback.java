package com.jean.redis.client.callback;

/**
 * @author jinshubao
 */
public interface TaskCallback<R> {

    /**
     * 回调
     *
     * @param params
     */
    void call(R params);
}
