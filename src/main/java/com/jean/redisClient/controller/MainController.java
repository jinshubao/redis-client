package com.jean.redisClient.controller;

import com.jean.redisClient.Service.DelService;
import com.jean.redisClient.Service.DetailService;
import com.jean.redisClient.Service.ListService;
import com.jean.redisClient.factory.ListCellFactory;
import com.jean.redisClient.factory.TableCellFactory;
import com.jean.redisClient.factory.TreeCellFactory;
import com.jean.redisClient.model.*;
import com.jean.redisClient.utils.Utils;
import javafx.beans.binding.ObjectBinding;
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
@SuppressWarnings("unchecked")
public class MainController implements Initializable {

    @FXML
    public TextField keyword;
    @FXML
    public Button search;
    @FXML
    public ListView<String> detail;
    @FXML
    public TreeView<NodeModel> tree;
    @FXML
    public TableView<BaseModel> table;
    @FXML
    public ProgressIndicator progress;
    @FXML
    public Label message;
    @FXML
    private SplitPane splitPane;

    @Autowired
    private ListService listService;

    @Autowired
    private DetailService detailService;

    @Autowired
    private TableCellFactory tableCellFactory;

    @Autowired
    private ListCellFactory listCellFactory;

    @Autowired
    private TreeCellFactory treeCellFactory;

    @Autowired
    private DelService delService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        progress.visibleProperty().bind(listService.runningProperty().or(detailService.runningProperty()));

        search.disableProperty().bind(listService.runningProperty().or(detailService.runningProperty()));
        search.setOnAction(event -> {
            Map<String, Object> params = new HashMap<>();
            params.put("cmd", keyword.getText());
            listService.setParams(params);
            listService.restart();
        });

        tree.setCellFactory(treeCellFactory);
        tree.setRoot(new TreeItem<>(new RootModel("服务器列表")));
        tree.getRoot().setExpanded(true);
        TreeItem<NodeModel> localhost = new TreeItem<>(new HostModel("localhost", 6379));
        TreeItem<NodeModel> dev = new TreeItem<>(new HostModel("10.52.2.170", 6380, "90-=op[]"));
        TreeItem<NodeModel> test = new TreeItem<>(new HostModel("10.52.2.170", 6379, "90-=op[]"));
        tree.getRoot().getChildren().addAll(localhost, dev, test);
        tree.getRoot().getChildren().forEach(node -> node.getChildren().addAll(Utils.getDbItems((HostModel) node.getValue())));

        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            System.out.println(newValue.toString());
            if (newValue.getValue() instanceof HostModel || newValue.getValue() instanceof DbModel) {
                //切换连接
                dbChange();
                listService.addParams("cmd", "*");
                listService.restart();
            }
        });

        table.getColumns().get(0).setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getKey()));
        table.getColumns().get(1).setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getType()));
        table.getColumns().get(2).setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getSize()));
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            detailService.addParams("item", newValue);
            detailService.restart();
        });
        table.getColumns().get(0).setCellFactory(tableCellFactory);

        detail.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        detail.setCellFactory(listCellFactory);

        listService.setOnRunning(event -> table.getItems().clear());
        listService.setOnSucceeded(event -> {
            List<BaseModel> list = (List<BaseModel>) listService.getValue();
            table.getItems().addAll(list);
            //生成树形结构
        });

        detailService.setOnSucceeded(event -> {
            DetailModel detail = (DetailModel) detailService.getValue();
            if (detail != null) {
                this.detail.getItems().add("key : " + detail.getKey() + "\ttype : " + detail.getType() + "\tsize : " + detail.getSize() + "\tttl : " + detail.getTtl() + " s");
                detail.getValues().forEach(item -> this.detail.getItems().add(item));
            }
        });
        detailService.setOnRunning(event -> detail.getItems().clear());

        delService.setOnSucceeded(event -> {
            BaseModel item = (BaseModel) delService.getValue();
            if (item != null) {
                table.getItems().remove(item);
            }
        });
    }

    private void dbChange() {
        DbModel dbModel = null;
        TreeItem<NodeModel> selectedItem = tree.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            NodeModel value = selectedItem.getValue();
            if (value instanceof HostModel) {
                dbModel = new DbModel((HostModel) value, 0);
            } else if (value instanceof DbModel) {
                dbModel = (DbModel) value;
            }
        }
        listService.setDbModel(dbModel);
        detailService.setDbModel(dbModel);
        delService.setDbModel(dbModel);
    }
}
