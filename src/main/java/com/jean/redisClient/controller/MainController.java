package com.jean.redisClient.controller;

import com.jean.redisClient.Service.DelService;
import com.jean.redisClient.Service.DetailService;
import com.jean.redisClient.Service.ListService;
import com.jean.redisClient.constant.CommonConstant;
import com.jean.redisClient.factory.ListCellFactory;
import com.jean.redisClient.factory.TableCellFactory;
import com.jean.redisClient.factory.TreeCellFactory;
import com.jean.redisClient.model.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.*;

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
    public TableView<ListModel> table;
    @FXML
    public ProgressIndicator progress;
    @FXML
    public Label message;
    @FXML
    public SplitPane splitPane;

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

        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            if (newValue.getValue() instanceof DbModel) {
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

        listService.setOnScheduled(event -> {
            table.getItems().clear();
            message.textProperty().unbind();
            message.textProperty().bind(listService.messageProperty());
        });
        listService.setOnSucceeded(event -> {
            List<ListModel> list = (List<ListModel>) listService.getValue();
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
        detailService.setOnScheduled(event -> {
            detail.getItems().clear();
            message.textProperty().unbind();
            message.textProperty().bind(detailService.messageProperty());
        });

        delService.setOnSucceeded(event -> {
            ListModel item = (ListModel) delService.getValue();
            if (item != null) {
                table.getItems().remove(item);
            }
        });

        //读配置文件
        List<HostModel> hostModels = new ArrayList<>();

        hostModels.forEach(hostModel ->
                tree.getRoot().getChildren().add(new TreeItem<>(new HostModel(hostModel.getHostName(), hostModel.getPort(), hostModel.getAuth()))));
    }

    /**
     * 切换db
     */
    private void dbChange() {
        DbModel dbModel = null;
        TreeItem<NodeModel> selectedItem = tree.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            NodeModel value = selectedItem.getValue();
            if (value instanceof DbModel) {
                dbModel = (DbModel) value;
            }
        }
        listService.setDbModel(dbModel);
        detailService.setDbModel(dbModel);
        delService.setDbModel(dbModel);
    }

    //主程序退出回调
    public void close() {
        //
        System.out.println("退出了。。。。");
    }
}
