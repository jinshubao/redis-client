package com.jean.redis.client.dialog;

import com.jean.redis.client.util.ResourceLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * 服务器信息对话框
 */
public class RedisServerInfoDialog extends Dialog<Void> {

    public RedisServerInfoDialog(String defaultValue) {
        DialogPane dialogPane = getDialogPane();
        TextArea textArea = new TextArea();
        textArea.setPrefHeight(400.0);
        textArea.setEditable(false);
        textArea.setText(defaultValue);
        this.setTitle("服务器信息");
        this.setHeaderText("服务器信息");
        this.initModality(Modality.APPLICATION_MODAL);
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(new Image(ResourceLoader.loadAsStream("/image/dbs_redis_24px.png")));
        getDialogPane().setContent(textArea);
        dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);
    }
}
