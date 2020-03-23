package com.jean.redis.client.controller;

import com.jean.redis.client.factory.*;
import com.jean.redis.client.model.RedisKey;
import com.jean.redis.client.model.RedisValue;
import com.jean.redis.client.util.MessageUtils;
import com.jean.redis.client.util.ViewUtils;
import com.jean.redis.client.view.ProgressIndicatorPlaceholder;
import com.jean.redis.client.view.RedisRootItem;
import com.jean.redis.client.view.action.IMouseAction;
import com.jean.redis.client.view.handler.IMenuBarActionEventHandler;
import com.jean.redis.client.view.handler.impl.MenuBarActionEventHandlerImpl;
import com.jean.redis.client.view.handler.impl.RedisKeyActionEventHandlerImpl;
import com.jean.redis.client.view.handler.impl.RedisRootItemActionEventHandler;
import com.jean.redis.client.view.handler.impl.RedisValueActionEventHandlerImpl;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author jinshubao
 */
@SuppressWarnings("unchecked")
public class MainController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    public BorderPane root;
    public Label serverInfoLabel;
    public TextField keyTextFiled;
    public TextArea valueTextArea;
    public Button saveButton;
    public GridPane valueGridPane;
    public SplitPane valueSplitPane;
    public SplitPane splitPane;
    public TextField keywordTextFiled;
    public Button searchButton;
    public TreeView<Object> serverTreeView;
    public TableView<RedisKey> keyTableView;
    public TableColumn<RedisKey, Integer> keyNoColumn;
    public TableColumn<RedisKey, byte[]> keyColumn;
    public TableColumn<RedisKey, String> typeColumn;
    public TableColumn<RedisKey, Number> sizeColumn;
    public TableColumn<RedisKey, Number> ttlColumn;
    public TableView<RedisValue> valueTableView;
    public TableColumn<RedisValue, Integer> valueNoColumn;
    public TableColumn<RedisValue, byte[]> valueKeyColumn;
    public TableColumn<RedisValue, byte[]> valueColumn;
    public TableColumn<RedisValue, Number> valueScoreColumn;
    public Label messageLabel;
    public MenuItem exitMenuItem;
    public MenuItem aboutMenuItem;


    public MainController() {
        ViewUtils.init(this);
    }

    /**
     * @param url    url
     * @param bundle bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle bundle) {

        IMenuBarActionEventHandler menuBarActionEventHandler = new MenuBarActionEventHandlerImpl();
        exitMenuItem.setOnAction(event -> menuBarActionEventHandler.exit());
        aboutMenuItem.setOnAction(event -> menuBarActionEventHandler.about());

        RedisRootItem rootItem = new RedisRootItem("服务器列表", new RedisRootItemActionEventHandler());
        rootItem.setExpanded(true);
        serverTreeView.setCellFactory(new TreeCellFactory());
        serverTreeView.setRoot(rootItem);
        //切换数据库
        serverTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof IMouseAction) {
                ((IMouseAction) newValue).select();
            }
        });

        TableViewByteColumnCellFactory tableViewByteColumnCellFactory = new TableViewByteColumnCellFactory();
        TableViewRowIndexColumnCellFactory tableViewRowIndexColumnCellFactory = new TableViewRowIndexColumnCellFactory();

        keyNoColumn.setCellFactory(tableViewRowIndexColumnCellFactory);
        keyColumn.setCellFactory(tableViewByteColumnCellFactory);
        keyColumn.setCellValueFactory(param -> param.getValue().keyProperty());
        typeColumn.setCellValueFactory(param -> param.getValue().typeProperty());
        sizeColumn.setCellValueFactory(param -> param.getValue().sizeProperty());
        ttlColumn.setCellValueFactory(param -> param.getValue().ttlProperty());
        keyTableView.setPlaceholder(new ProgressIndicatorPlaceholder());
        keyTableView.setRowFactory(new RedisKeyTableRowFactory(new RedisKeyActionEventHandlerImpl()));

        valueNoColumn.setCellFactory(tableViewRowIndexColumnCellFactory);
        valueScoreColumn.setCellValueFactory(param -> param.getValue().scoreProperty());
        valueKeyColumn.setCellFactory(tableViewByteColumnCellFactory);
        valueKeyColumn.setCellValueFactory(param -> param.getValue().keyProperty());
        valueColumn.setCellFactory(tableViewByteColumnCellFactory);
        valueColumn.setCellValueFactory(param -> param.getValue().valueProperty());
        valueTableView.setPlaceholder(new ProgressIndicatorPlaceholder());
        valueTableView.setRowFactory(new RedisValueTableRowFactory(new RedisValueActionEventHandlerImpl()));
        MessageUtils.init(messageLabel);
    }

    public BorderPane getRoot() {
        return root;
    }

    public Button getSearchButton() {
        return searchButton;
    }

    public TreeView<Object> getServerTreeView() {
        return serverTreeView;
    }

    public TableView<RedisKey> getKeyTableView() {
        return keyTableView;
    }

    public TableColumn<RedisKey, Integer> getKeyNoColumn() {
        return keyNoColumn;
    }

    public TableColumn<RedisKey, byte[]> getKeyColumn() {
        return keyColumn;
    }

    public TableColumn<RedisKey, String> getTypeColumn() {
        return typeColumn;
    }

    public TableColumn<RedisKey, Number> getSizeColumn() {
        return sizeColumn;
    }

    public TableColumn<RedisKey, Number> getTtlColumn() {
        return ttlColumn;
    }

    public TableView<RedisValue> getValueTableView() {
        return valueTableView;
    }

    public TableColumn<RedisValue, Integer> getValueNoColumn() {
        return valueNoColumn;
    }

    public TableColumn<RedisValue, byte[]> getValueKeyColumn() {
        return valueKeyColumn;
    }

    public TableColumn<RedisValue, byte[]> getValueColumn() {
        return valueColumn;
    }

    public TableColumn<RedisValue, Number> getValueScoreColumn() {
        return valueScoreColumn;
    }

    public Label getMessageLabel() {
        return messageLabel;
    }

    public MenuItem getExitMenuItem() {
        return exitMenuItem;
    }

    public MenuItem getAboutMenuItem() {
        return aboutMenuItem;
    }

    public Label getServerInfoLabel() {
        return serverInfoLabel;
    }

    public TextField getKeyTextFiled() {
        return keyTextFiled;
    }

    public TextArea getValueTextArea() {
        return valueTextArea;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public GridPane getValueGridPane() {
        return valueGridPane;
    }

    public SplitPane getValueSplitPane() {
        return valueSplitPane;
    }

    public SplitPane getSplitPane() {
        return splitPane;
    }

    public TextField getKeywordTextFiled() {
        return keywordTextFiled;
    }

}
