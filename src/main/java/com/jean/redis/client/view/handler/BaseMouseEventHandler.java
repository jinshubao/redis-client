package com.jean.redis.client.view.handler;

import javafx.scene.Node;

public abstract class BaseMouseEventHandler<T> extends BaseEventHandler implements IMouseActionEventHandler<T> {

    public BaseMouseEventHandler(Node root) {
        super(root);
    }

    @Override
    public void onClick(T t) {

    }

    @Override
    public void onDoubleClick(T t) {

    }

    @Override
    public void onSelected(T t) {

    }
}
