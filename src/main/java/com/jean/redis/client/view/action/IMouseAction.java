package com.jean.redis.client.view.action;

import com.jean.redis.client.view.handler.IMouseEventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * @author jinshubao
 */
public interface IMouseAction {

    default void click(MouseEvent event) {
        IMouseEventHandler handler = this.getMouseEventHandler();
        if (handler != null) {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getClickCount() == 1) {
                    handler.onSelected(this);
                } else if (event.getClickCount() == 2) {
                    handler.onDoubleClick(this);
                }
            }
        }
    }

    default void select() {
        IMouseEventHandler handler = this.getMouseEventHandler();
        if (handler != null) {
            handler.onSelected(this);
        }
    }


    default IMouseEventHandler getMouseEventHandler() {
        return null;
    }

}
