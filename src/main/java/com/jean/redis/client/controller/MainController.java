package com.jean.redis.client.controller;

import com.jean.redis.client.Service.DetailService;
import com.jean.redis.client.Service.ListService;
import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.entry.Node;
import com.jean.redis.client.factory.DetailListCellFactory;
import com.jean.redis.client.factory.TableCellFactory;
import com.jean.redis.client.factory.TreeCellFactory;
import com.jean.redis.client.model.DBNode;
import com.jean.redis.client.model.ListItem;
import com.jean.redis.client.model.RootNode;
import io.lettuce.core.AbstractRedisClient;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.Closeable;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author jinshubao
 */
@Controller
@SuppressWarnings("unchecked")
public class MainController implements Initializable, Closeable {

    static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML
    public TextField keyword;
    @FXML
    public Button search;
    @FXML
    public ListView<String> detail;
    @FXML
    public TreeView<Node> tree;
    @FXML
    public TableView<ListItem> table;
    @FXML
    public Label message;
    @FXML
    public SplitPane splitPane;
    @FXML
    public Label dbInfo;
    @FXML
    public MenuItem newConnection;
    @FXML
    public MenuItem exit;
    @FXML
    public MenuItem about;

    @Autowired
    private ListService listService;

    @Autowired
    private DetailService detailService;

    @Autowired
    private TableCellFactory tableCellFactory;

    /**
     * init
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        newConnection.setOnAction(event -> {

        });

        exit.setOnAction(event -> {
        });

        about.setOnAction(event -> {

        });
        search.disableProperty().bind(listService.runningProperty().or(detailService.runningProperty()));
        search.setOnAction(event -> {

        });

        tree.setCellFactory(new TreeCellFactory());
        tree.setRoot(new TreeItem<>(new RootNode("服务器列表")));
        tree.getRoot().setExpanded(true);

        //切换数据库
        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            detail.getItems().clear();
            if (newValue == null) {
                dbInfo.setText(null);
                return;
            }
            Node model = newValue.getValue();
            dbInfo.setText(model.toString());
            if (model instanceof DBNode) {
                listService.restart(((DBNode) model).getConfig(), "*", 100L);
            }
        });
//        tree.disableProperty().bind(listService.runningProperty());

        table.getColumns().get(0).setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getKey()));
        table.getColumns().get(1).setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getType()));
        table.getColumns().get(2).setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getSize()));
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                detailService.restart(newValue.getConfig(), newValue.getKey());
            }
        });
        table.getColumns().get(0).setCellFactory(tableCellFactory);
        ProgressIndicator listProgress = new ProgressIndicator(-1D);
        listProgress.setMaxHeight(50D);
        listProgress.setMaxWidth(50D);
        listProgress.visibleProperty().bind(listService.runningProperty());
        table.setPlaceholder(listProgress);

        ProgressIndicator detailProgress = new ProgressIndicator(-1D);
        detailProgress.setMaxHeight(50D);
        detailProgress.setMaxWidth(50D);
        detailProgress.visibleProperty().bind(detailService.runningProperty());
        detail.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        detail.setCellFactory(new DetailListCellFactory<>());
        detail.setPlaceholder(detailProgress);

        listService.setOnScheduled(event -> {
            table.getItems().clear();
            message.textProperty().unbind();
            message.textProperty().bind(listService.messageProperty());
        });
        listService.setOnSucceeded(event -> {
            List<String> list = (List<String>) listService.getValue();
            list.forEach(item -> table.getItems().add(new ListItem(listService.getConfig(), item, "", 0L)));
        });

        detailService.setOnScheduled(event -> {
            detail.getItems().clear();
            message.textProperty().unbind();
            message.textProperty().bind(detailService.messageProperty());
        });

        detailService.setOnSucceeded(event -> {
            this.detail.getItems().addAll((List<String>) detailService.getValue());
        });

        //读配置文件
        this.initConfig();
    }


    /**
     * 主程序退出回调
     */
    public void initConfig() {
        //TODO 保存配置

    }


    /**
     * 主程序退出回调
     */
    @Override
    public void close() {
        //TODO 保存配置
        logger.debug("main controller close...");
        CommonConstant.REDIS_CLIENT_MAP.values().forEach(AbstractRedisClient::shutdown);
    }
}
