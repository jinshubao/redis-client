package com.jean.redis.client.dialog;

import com.jean.redis.client.model.RedisServerProperty;
import com.jean.redis.client.util.ResourceLoader;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * 新建服务器对话框
 */
public class CreateRedisServerDialog extends Dialog<RedisServerProperty> {

    private final Label hostLabel;
    private final TextField hostFiled;

    private final Label portLabel;
    private final TextField portFiled;

    private final Label passwordLabel;
    private final PasswordField passwordField;

    private final GridPane grid;

    public CreateRedisServerDialog(RedisServerProperty defaultValue) {

        final DialogPane dialogPane = getDialogPane();

        hostLabel = new Label("host:");
        hostFiled = new TextField("127.0.0.1");
        portLabel = new Label("port:");
        portFiled = new TextField("6379");
        passwordLabel = new Label("password:");
        passwordField = new PasswordField();

        this.hostFiled.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(hostFiled, Priority.ALWAYS);
        GridPane.setFillWidth(hostFiled, true);

        this.portFiled.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(portFiled, Priority.ALWAYS);
        GridPane.setFillWidth(portFiled, true);

        this.passwordField.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(passwordField, Priority.ALWAYS);
        GridPane.setFillWidth(passwordField, true);


        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));
        updateGrid();

        this.setTitle("添加服务器");
        this.initModality(Modality.APPLICATION_MODAL);
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(new Image(ResourceLoader.Image.redis_logo_24));
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        //设置默认值
        if (defaultValue != null) {
            hostFiled.setText(defaultValue.getHost());
            portFiled.setText(String.valueOf(defaultValue.getPort()));
            passwordField.setText(defaultValue.getPassword());
        }

        //结果转换器
        setResultConverter(dialogButton -> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            if (data == ButtonBar.ButtonData.OK_DONE) {
                RedisServerProperty property = new RedisServerProperty();
                property.setHost(hostFiled.getText());
                String port = portFiled.getText();
                if (port != null && !port.isEmpty()) {
                    property.setPort(Integer.valueOf(port));
                }
                String password = passwordField.getText();
                if (password != null) {
                    property.setPassword(password);
                }
                return property;
            }
            return null;
        });
    }

    private void updateGrid() {
        grid.add(hostLabel, 0, 0);
        grid.add(hostFiled, 1, 0);
        grid.add(portLabel, 0, 1);
        grid.add(portFiled, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);
        getDialogPane().setContent(grid);
        Platform.runLater(hostFiled::requestFocus);
    }

    public static CreateRedisServerDialog newInstance(RedisServerProperty defaultValue) {
        return new CreateRedisServerDialog(defaultValue);
    }

    public static CreateRedisServerDialog newInstance() {
        return new CreateRedisServerDialog(null);
    }
}
