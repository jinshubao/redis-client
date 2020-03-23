package com.jean.redis.client.util;

import com.jean.redis.client.controller.MainController;
import com.jean.redis.client.model.RedisKey;
import com.jean.redis.client.model.RedisValue;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewUtils {

    private MainController mainController;

    private static ViewUtils instance = null;

    private ViewUtils(MainController mainController) {
        this.mainController = mainController;
    }

    public static void init(MainController mainController) {
        if (instance == null) {
            instance = new ViewUtils(mainController);
        } else {
            throw new RuntimeException("不能重复初始化");
        }
    }

    public static ViewUtils getInstance() {
        return instance;
    }

    public BorderPane getRoot() {
        return mainController.getRoot();
    }

    public Button getSearchButton() {
        return mainController.getSearchButton();
    }

    public TreeView<Object> getServerTreeView() {
        return mainController.getServerTreeView();
    }

    public TableView<RedisKey> getKeyTableView() {
        return mainController.getKeyTableView();
    }

    public TableColumn<RedisKey, Integer> getKeyNoColumn() {
        return mainController.getKeyNoColumn();
    }

    public TableColumn<RedisKey, byte[]> getKeyColumn() {
        return mainController.getKeyColumn();
    }

    public TableColumn<RedisKey, String> getTypeColumn() {
        return mainController.getTypeColumn();
    }

    public TableColumn<RedisKey, Number> getSizeColumn() {
        return mainController.getSizeColumn();
    }

    public TableColumn<RedisKey, Number> getTtlColumn() {
        return mainController.getTtlColumn();
    }

    public TableView<RedisValue> getValueTableView() {
        return mainController.getValueTableView();
    }

    public TableColumn<RedisValue, Integer> getValueNoColumn() {
        return mainController.getValueNoColumn();
    }

    public TableColumn<RedisValue, byte[]> getValueKeyColumn() {
        return mainController.getValueKeyColumn();
    }

    public TableColumn<RedisValue, byte[]> getValueColumn() {
        return mainController.getValueColumn();
    }

    public TableColumn<RedisValue, Number> getValueScoreColumn() {
        return mainController.getValueScoreColumn();
    }

    public Label getMessageLabel() {
        return mainController.getMessageLabel();
    }

    public MenuItem getExit() {
        return mainController.getExitMenuItem();
    }

    public MenuItem getAboutMenuItem() {
        return mainController.getAboutMenuItem();
    }

    public Label getServerInfoLabel() {
        return mainController.getServerInfoLabel();
    }

    public TextField getKeyTextFiled() {
        return mainController.getKeyTextFiled();
    }

    public TextArea getValueTextArea() {
        return mainController.getValueTextArea();
    }

    public Button getSaveButton() {
        return mainController.getSaveButton();
    }

    public GridPane getValueGridPane() {
        return mainController.getValueGridPane();
    }

    public SplitPane getValueSplitPane() {
        return mainController.getValueSplitPane();
    }

    public SplitPane getSplitPane() {
        return mainController.getSplitPane();
    }

    public TextField getKeywordTextFiled() {
        return mainController.getKeywordTextFiled();
    }


    public void initialize(URL url, ResourceBundle bundle) {
        mainController.initialize(url, bundle);
    }

}
