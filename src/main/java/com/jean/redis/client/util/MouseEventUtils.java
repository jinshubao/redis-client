package com.jean.redis.client.util;

import javafx.scene.input.MouseEvent;

public abstract class MouseEventUtils {

    public static boolean isDoubleClick(MouseEvent mouseEvent) {
        return mouseEvent != null && mouseEvent.getClickCount() == 2;
    }

}
