package com.jean.redis.client.factory;


import com.jean.redis.client.entry.Node;
import com.jean.redis.client.entry.NodeType;
import com.jean.redis.client.model.DirNode;
import com.jean.redis.client.model.HostNode;
import com.jean.redis.client.utils.TreeItemUtils;
import com.jean.redis.client.Service.DelService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jinshubao on 2016/11/25.
 */
@Component
public class TreeCellFactory implements Callback<TreeView<Node>, TreeCell<Node>> {

    private final DelService delService;

    @Autowired
    public TreeCellFactory(DelService delService) {
        this.delService = delService;
    }

    @Override
    public TreeCell<Node> call(TreeView<Node> param) {

        return new TreeCell<Node>() {
            @Override
            protected void updateItem(Node item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    TreeItem<Node> treeItem = getTreeItem();
                    ContextMenu contextMenu = new ContextMenu();
                    if (item.getNodeType() == NodeType.ROOT) {
                        MenuItem add = new MenuItem("新建连接");
                        contextMenu.getItems().add(add);
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
                                    treeItem.getChildren().add(TreeItemUtils.createHostNode(new HostNode(h, p, a)));
                                }
                            });
                        });
                    } else if (item.getNodeType() == NodeType.HOST) {
                        MenuItem del = new MenuItem("删除连接");
                        contextMenu.getItems().add(del);
                        del.setOnAction(t -> treeItem.getParent().getChildren().remove(treeItem));
                    } else if (item instanceof DirNode) {
                        MenuItem del = new MenuItem("删除");
                        contextMenu.getItems().add(del);
                        DirNode dirNode = (DirNode) item;
                        del.setOnAction(t -> {
                            delService.restart(dirNode, dirNode.getFullPath() + "*", treeItem);
                        });
                    }
                    if (!contextMenu.getItems().isEmpty()) {
                        setContextMenu(contextMenu);
                    }
                    setText(item.toString());
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
