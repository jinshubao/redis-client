package com.jean.redis.client.view.handler;

import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseEventHandler {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Node parent;


    protected BaseEventHandler(Node parent) {
        this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    protected <N extends Node> N lookup(String selector) {
        return (N) this.parent.lookup(selector);
    }

    public Node getParent() {
        return parent;
    }

    public Logger getLogger() {
        return logger;
    }
}
