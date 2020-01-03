package com.jean.redis.client.factory.menu;

import com.jean.redis.client.entry.Node;
import com.jean.redis.client.model.ConfigProperty;
import com.jean.redis.client.model.HostNode;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RootContextMenu extends AbstractTreeContextMenu {

    @Override
    public ContextMenu createContextMenu(TreeItem treeItem, Node node) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem add = new MenuItem("新建连接");
        contextMenu.getItems().add(add);
        add.setOnAction(t -> {
            Dialog<ButtonType> dialog = createDialog();
            dialog.showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    TextField host = (TextField) dialog.getDialogPane().getContent().lookup("#host");
                    TextField port = (TextField) dialog.getDialogPane().getContent().lookup("#port");
                    TextField auth = (TextField) dialog.getDialogPane().getContent().lookup("#auth");
                    String h = host.getText();
                    int p = Integer.parseInt(port.getText());
                    String a = auth.getText();
                    if (a == null || a.isEmpty()) {
                        a = null;
                    }
                    treeItem.getChildren().add(new TreeItem<>(new HostNode(new ConfigProperty(h, p, a, 0))));
                }
            });
        });
        return contextMenu;
    }

    private Dialog<ButtonType> createDialog() {
        TextField host = new TextField("127.0.0.1");
        host.setId("host");
        TextField port = new TextField("6379");
        port.setId("port");
        PasswordField auth = new PasswordField();
        auth.setText(null);
        auth.setId("auth");


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));

        host.setPromptText("host");
        port.setPromptText("port");
        auth.setPromptText("auth");

        grid.add(new Label("host:"), 0, 0);
        grid.add(host, 1, 0);
        grid.add(new Label("port:"), 0, 1);
        grid.add(port, 1, 1);
        grid.add(new Label("auth:"), 0, 2);
        grid.add(auth, 1, 2);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("添加服务器");
        dialog.initModality(Modality.APPLICATION_MODAL);
        DialogPane dialogPane = dialog.getDialogPane();
        Stage sta = (Stage) dialogPane.getScene().getWindow();
        sta.getIcons().add(new Image(this.getClass().getResourceAsStream("/image/dbs_redis_24px.png")));
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setContent(grid);
        return dialog;
    }
}
