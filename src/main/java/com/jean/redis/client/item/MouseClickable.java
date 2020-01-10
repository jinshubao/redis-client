package com.jean.redis.client.item;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;


public interface MouseClickable {


    EventHandler<MouseEvent> getClickEventHandler();
}
