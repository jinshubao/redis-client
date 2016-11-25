package com.jean.redisClient.controller;

import com.jean.redisClient.Service.DelService;
import com.jean.redisClient.Service.DetailService;
import com.jean.redisClient.Service.ListService;
import com.jean.redisClient.factory.DataCellFactory;
import com.jean.redisClient.model.BaseModel;
import com.jean.redisClient.model.DetailModel;
import com.jean.redisClient.model.NodeModel;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Controller
public class MainController implements Initializable {

    @FXML
    public TextField keyword;
    @FXML
    public Button search;
    @FXML
    public ListView<String> list;
    @FXML
    public TreeView<NodeModel> tree;
    @FXML
    public TableView<BaseModel> table;
    @FXML
    public ProgressIndicator progress;
    @FXML
    private SplitPane splitPane;

    @Autowired
    private ListService listService;

    @Autowired
    private DetailService detailService;

    @Autowired
    DataCellFactory dataCellFactory;

    @Autowired
    DelService delService;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        progress.visibleProperty().bind(listService.runningProperty().or(detailService.runningProperty()));
        search.disableProperty().bind(listService.runningProperty().or(detailService.runningProperty()));

        ((TableColumn<BaseModel, String>) table.getColumns().get(0)).setCellValueFactory(param -> {
            return param.getValue().keyProperty();
        });
        ((TableColumn<BaseModel, String>) table.getColumns().get(1)).setCellValueFactory(param -> {
            return param.getValue().typeProperty();
        });
        ((TableColumn<BaseModel, Long>) table.getColumns().get(2)).setCellValueFactory(param -> {
            return new SimpleObjectProperty<>(param.getValue().getSize());
        });

        table.getColumns().get(0).setCellFactory(dataCellFactory);

        search.setOnAction(event -> {
            table.getItems().clear();
            listService.clearParams();
            Map<String, Object> params = new HashMap<>();
            params.put("cmd", keyword.getText());
            listService.setParams(params);
            listService.restart();
        });

        listService.setOnSucceeded(event -> {
            List<BaseModel> list = (List<BaseModel>) listService.getValue();
            table.getItems().addAll(list);
            //生成树形结构
        });

        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            list.getItems().clear();
            detailService.clearParams();
            Map<String, Object> params = new HashMap<>();
            params.put("model", newValue);
            detailService.setParams(params);
            detailService.restart();
        });

        detailService.setOnSucceeded(event -> {
            list.getItems().clear();
            DetailModel detail = (DetailModel) detailService.getValue();
            if (detail != null) {
                list.getItems().add("key  : " + detail.getKey());
                list.getItems().add("value: " + detail.getValue());
                list.getItems().add("type : " + detail.getType());
                list.getItems().add("size : " + detail.getSize());
            }
        });

        delService.setOnSucceeded(event -> {
            BaseModel item = (BaseModel) delService.getValue();
            if (item != null) {
                table.getItems().remove(item);
            }
        });

        TreeItem<NodeModel> root = new TreeItem<>(new NodeModel("服务器列表"));

        tree.setRoot(root);
        tree.getRoot().setExpanded(true);

        TreeItem<NodeModel> localhost = new TreeItem<>(new NodeModel("localhost"));
        TreeItem<NodeModel> dev = new TreeItem<>(new NodeModel("dev"));

        TreeItem<NodeModel> test = new TreeItem<>(new NodeModel("test"));
        tree.getRoot().getChildren().addAll(localhost, dev, test);
        tree.getRoot().getChildren().forEach(node -> {
            for (int i = 0; i < 16; i++) {
                node.getChildren().add(new TreeItem<>(new NodeModel("db" + i)));
            }
        });
    }
}
