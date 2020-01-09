package com.jean.redis.client.ctrl;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

public class ProgressIndicatorPlaceholder extends StackPane {

    private final ProgressIndicator progressIndicator;
    private final Label text;

    public ProgressIndicatorPlaceholder() {
        text = new Label("暂无内容");
        progressIndicator = new ProgressIndicator(-1D);
        progressIndicator.setMaxHeight(50D);
        progressIndicator.setMaxWidth(50D);
        progressIndicator.setVisible(false);
        text.visibleProperty().bind(progressIndicator.visibleProperty().not());
        this.getChildren().addAll(text, progressIndicator);
    }

    public DoubleProperty indicatorProgressProperty() {
        return progressIndicator.progressProperty();
    }

    public BooleanProperty indicatorVisibleProperty() {
        return progressIndicator.visibleProperty();
    }

    public StringProperty textProperty() {
        return text.textProperty();
    }

}
