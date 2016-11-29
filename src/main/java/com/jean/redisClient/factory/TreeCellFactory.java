package com.jean.redisClient.factory;


import com.jean.redisClient.model.HostModel;
import com.jean.redisClient.model.NodeModel;
import com.jean.redisClient.model.RootModel;
import com.jean.redisClient.utils.TreeItemUtils;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.util.Callback;
import org.springframework.stereotype.Component;

/**
 * Created by jinshubao on 2016/11/25.
 */
@Component
public class TreeCellFactory implements Callback<TreeView<NodeModel>, TreeCell<NodeModel>> {

    @Override
    public TreeCell<NodeModel> call(TreeView<NodeModel> param) {

        return new TreeCell<NodeModel>() {
            @Override
            protected void updateItem(NodeModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    TreeItem<NodeModel> treeItem = getTreeItem();
                    ContextMenu contextMenu = new ContextMenu();
                    if (item instanceof RootModel) {
                        MenuItem add = new MenuItem("新增服务器");
                        contextMenu.getItems().add(add);
                        TreeItem<NodeModel> root = getTreeItem();
                        add.setOnAction(t -> {
                            Dialog dialog = createDialog();
                            dialog.showAndWait().ifPresent(buttonType -> {
                                if (buttonType == ButtonType.OK) {
                                    TextField host = (TextField) dialog.getDialogPane().getContent().lookup("#host");
                                    TextField port = (TextField) dialog.getDialogPane().getContent().lookup("#port");
                                    TextField auth = (TextField) dialog.getDialogPane().getContent().lookup("#auth");
                                    String h = host.getText();
                                    Integer p = Integer.parseInt(port.getText());
                                    String a = auth.getText();
                                    if (a == null || a.isEmpty()) {
                                        a = null;
                                    }
                                    root.getChildren().add(TreeItemUtils.createHostTreeItem(new HostModel(h, p, a)));
                                }
                            });

                        });
                    } else if (item instanceof HostModel) {
                        MenuItem del = new MenuItem("删除");
                        contextMenu.getItems().add(del);
                        del.setOnAction(t -> treeItem.getParent().getChildren().remove(treeItem));
                    }
                    setContextMenu(contextMenu);
                    setText(item == null ? "" : item.toString());
                }
            }
        };
    }

    private Dialog createDialog() {
        TextField host = new TextField("localhost");
        host.setId("host");
        TextField port = new TextField("6379");
        port.setId("port");
        PasswordField auth = new PasswordField();
        auth.setId("auth");

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("添加服务器");
        dialog.initModality(Modality.APPLICATION_MODAL);
        DialogPane dialogPane = new DialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

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
        dialogPane.setContent(grid);
        dialog.setDialogPane(dialogPane);
        return dialog;
    }

}
