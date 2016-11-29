package com.jean.redisClient.factory;


import com.jean.redisClient.model.HostModel;
import com.jean.redisClient.model.NodeModel;
import com.jean.redisClient.model.RootModel;
import com.jean.redisClient.utils.Utils;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;
import javafx.util.Callback;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
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
                        MenuItem add = new MenuItem("新增链接");
                        contextMenu.getItems().add(add);
                        add.setOnAction(t -> {
                            HostModel hostModel = new HostModel("localhost", 6379);
                            TreeItem<NodeModel> host = new TreeItem<>(hostModel);
                            host.getChildren().addAll(Utils.getDbItems(hostModel));
                            treeItem.getChildren().add(host);

                            Window owner = getTreeView().getScene().getWindow();
                            // define pages to show
                            Wizard wizard = new Wizard(owner);
                            wizard.setTitle("Linear Wizard");

                            // --- page 1
                            int row = 0;

                            GridPane page1Grid = new GridPane();
                            page1Grid.setVgap(10);
                            page1Grid.setHgap(10);

                            page1Grid.add(new Label("First Name:"), 0, row);
                            TextField txFirstName = createTextField("firstName");
//        wizard.getValidationSupport().registerValidator(txFirstName, Validator.createEmptyValidator("First Name is mandatory"));
                            page1Grid.add(txFirstName, 1, row++);

                            page1Grid.add(new Label("Last Name:"), 0, row);
                            TextField txLastName = createTextField("lastName");
//        wizard.getValidationSupport().registerValidator(txLastName, Validator.createEmptyValidator("Last Name is mandatory"));
                            page1Grid.add(txLastName, 1, row);

                            WizardPane page1 = new WizardPane();
                            page1.setHeaderText("Please Enter Your Details");
                            page1.setContent(page1Grid);


                            // --- page 2
                            final WizardPane page2 = new WizardPane() {
                                @Override
                                public void onEnteringPage(Wizard wizard) {
                                    String firstName = (String) wizard.getSettings().get("firstName");
                                    String lastName = (String) wizard.getSettings().get("lastName");

                                    setContentText("Welcome, " + firstName + " " + lastName + "! Let's add some newlines!\n\n\n\n\n\n\nHello World!");
                                }
                            };
                            page2.setHeaderText("Thanks For Your Details!");


                            // --- page 3
                            WizardPane page3 = new WizardPane();
                            page3.setHeaderText("Goodbye!");
                            page3.setContentText("Page 3, with extra 'help' button!");

                            ButtonType helpDialogButton = new ButtonType("Help", ButtonBar.ButtonData.HELP_2);
                            page3.getButtonTypes().add(helpDialogButton);
                            Button helpButton = (Button) page3.lookupButton(helpDialogButton);
                            helpButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {
                                actionEvent.consume(); // stop hello.dialog from closing
                                System.out.println("Help clicked!");
                            });


                            // create wizard
                            wizard.setFlow(new Wizard.LinearFlow(page1, page2, page3));

                            System.out.println("page1: " + page1);
                            System.out.println("page2: " + page2);
                            System.out.println("page3: " + page3);

                            // show wizard and wait for response
                            wizard.showAndWait().ifPresent(result -> {
                                if (result == ButtonType.FINISH) {
                                    System.out.println("Wizard finished, settings: " + wizard.getSettings());
                                }
                            });

                        });
                        setContextMenu(contextMenu);
                    } else if (item instanceof HostModel) {
                        MenuItem del = new MenuItem("删除");
                        contextMenu.getItems().add(del);
                        del.setOnAction(t -> treeItem.getParent().getChildren().remove(treeItem));
                        setContextMenu(contextMenu);
                    }
                    setText(item == null ? "" : item.toString());
                }
            }
        };
    }

    private TextField createTextField(String id) {
        TextField textField = new TextField();
        textField.setId(id);
        GridPane.setHgrow(textField, Priority.ALWAYS);
        return textField;
    }
}
