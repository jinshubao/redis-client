package com.jean.redis.client.util;

import javafx.application.Platform;
import javafx.scene.control.Labeled;

public class MessageUtils {

    private static Labeled messageLabel;

    public static void init(Labeled messageLabel) {
        MessageUtils.messageLabel = messageLabel;
    }

    public static void updateMessage(String message) {
        if (Platform.isFxApplicationThread()) {
            messageLabel.setText(message);
        } else {
            Platform.runLater(() -> messageLabel.setText(message));
        }
    }
}
