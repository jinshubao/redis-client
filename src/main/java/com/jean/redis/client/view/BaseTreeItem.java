package com.jean.redis.client.view;

import com.jean.redis.client.view.handler.IMouseActionEventHandler;
import com.jean.redis.client.view.action.IContextMenu;
import com.jean.redis.client.view.action.IMouseAction;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public abstract class BaseTreeItem<T> extends TreeItem<T> implements IContextMenu, IMouseAction {


    private IMouseActionEventHandler mouseActionEventHandler;

    public BaseTreeItem(T value, IMouseActionEventHandler mouseActionEventHandler) {
        super(value);
        this.mouseActionEventHandler = mouseActionEventHandler;
    }

    public BaseTreeItem(T value, Node graphic, IMouseActionEventHandler mouseActionEventHandler) {
        super(value, graphic);
        this.mouseActionEventHandler = mouseActionEventHandler;
    }

    @Override
    public ContextMenu getContextMenu() {
        return null;
    }

    @Override
    public void click(MouseEvent event) {
        if (this.mouseActionEventHandler != null) {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getClickCount() == 1) {
                    this.mouseActionEventHandler.onClick(this);
                } else if (event.getClickCount() == 2) {
                    this.mouseActionEventHandler.onDoubleClick(this);
                }
            }
        }
    }

    @Override
    public void select() {
        this.mouseActionEventHandler.onSelected(this);
    }
}
