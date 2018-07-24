package com.jean.redis.client.controller;

import com.jean.redis.client.Service.DelService;
import com.jean.redis.client.Service.DetailService;
import com.jean.redis.client.Service.ListService;
import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.entry.Node;
import com.jean.redis.client.entry.NodeType;
import com.jean.redis.client.factory.ListCellFactory;
import com.jean.redis.client.factory.TableCellFactory;
import com.jean.redis.client.factory.TreeCellFactory;
import com.jean.redis.client.model.*;
import com.jean.redis.client.utils.TreeItemUtils;
import com.jean.redis.client.utils.YamlUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author jinshubao
 */
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

    @Autowired
    private ListCellFactory listCellFactory;

    @Autowired
    private TreeCellFactory treeCellFactory;

    @Autowired
    private DelService delService;


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
            Map<String, Object> params = new HashMap<>();
            params.put("cmd", keyword.getText());
            listService.setParams(params);
            listService.restart();
        });

        tree.setCellFactory(treeCellFactory);
        tree.setRoot(new TreeItem<>(new RootNode("服务器列表")));
        tree.getRoot().setExpanded(true);

        //切换数据库
        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            dbChange(newValue);
        });

        table.getColumns().get(0).setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getKey()));
        table.getColumns().get(1).setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getType()));
        table.getColumns().get(2).setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getSize()));
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                detailService.addParams("item", newValue);
                detailService.restart(newValue);
            }
        });
        table.getColumns().get(0).setCellFactory(tableCellFactory);
        ProgressIndicator listProgress = new ProgressIndicator(-1D);
        listProgress.setMaxHeight(50D);
        listProgress.setMaxWidth(50D);
        listProgress.visibleProperty().bind(listService.runningProperty());
        table.setPlaceholder(listProgress);

        detail.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        detail.setCellFactory(listCellFactory);
        ProgressIndicator detailProgress = new ProgressIndicator(-1D);
        detailProgress.setMaxHeight(50D);
        detailProgress.setMaxWidth(50D);
        detailProgress.visibleProperty().bind(detailService.runningProperty());
        detail.setPlaceholder(detailProgress);

        listService.setOnScheduled(event -> {
            table.getItems().clear();
            message.textProperty().unbind();
            message.textProperty().bind(listService.messageProperty());
        });
        listService.setOnSucceeded(event -> {
            List<ListItem> list = (List<ListItem>) listService.getValue();
            table.getItems().addAll(list);
            //生成树形结构
            TreeItemUtils.generateDir(listService.getTreeItem(), new ArrayList(table.getItems()));
            listService.getTreeItem().setExpanded(true);
        });

        detailService.setOnSucceeded(event -> {
            DetailItem detail = (DetailItem) detailService.getValue();
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
            Object item = delService.getValue();
            if (item instanceof Node) {
                Node node = (Node) item;
                if (node.getNodeType() == NodeType.DIR) {
                    Object binding = delService.getBinding();
                    if (binding instanceof TreeItem) {
                        ((TreeItem) binding).getParent().getChildren().remove(binding);
                        delService.setBinding(null);
                    }
                    table.getItems().clear();
                } else if (node.getNodeType() == NodeType.LIST) {
                    ListItem listItem = (ListItem) item;
                    table.getItems().remove(listItem);
                    detail.getItems().clear();
                }
            }
        });

        //读配置文件
        Map config = initConfig(CommonConstant.CONFIG_FILE_PATH + CommonConstant.CONFIG_FILE_NAME);
        config.forEach((key, m) -> {
            Map map = (Map) m;
            Object auth = map.get("auth");
            Object hostName = map.get("hostName");
            Object port = map.get("port");

            HostNode node = new HostNode();

            if (hostName != null) {
                node.setHostName(hostName.toString().trim());
            }
            if (port != null && !port.toString().trim().isEmpty()) {
                node.setPort(Integer.parseInt(port.toString().trim()));
            }
            if (auth != null) {
                node.setAuth(auth.toString());
            }
            tree.getRoot().getChildren().add(TreeItemUtils.createHostNode(node));
        });

        tree.disableProperty().bind(listService.runningProperty());
    }

    /**
     * 切换db
     *
     * @param newValue
     */
    private void dbChange(TreeItem<Node> newValue) {
        detail.getItems().clear();
        if (newValue == null) {
            dbInfo.setText(null);
            return;
        }
        Node model = newValue.getValue();
        dbInfo.setText(model.toString());
        if (model instanceof DBNode) {
            listService.restart(newValue);
        }
    }

    /**
     * 主程序退出回调
     *
     * @throws IOException
     */
    public void close() throws IOException {
        //保存配置
        Map configs = new LinkedHashMap<>();
        ObservableList<TreeItem<Node>> treeItems = tree.getRoot().getChildren();
        for (int i = 0; i < treeItems.size(); i++) {
            HostNode value = (HostNode) treeItems.get(i).getValue();
            Map map = new HashMap();
            map.put("hostName", value.getHostName());
            map.put("port", value.getPort());
            if (value.getAuth() != null) {
                map.put("auth", value.getAuth());
            }
            configs.put("host" + i, map);
        }
        YamlUtils.write(configs, CommonConstant.CONFIG_FILE_PATH, CommonConstant.CONFIG_FILE_NAME);
    }

    private Map<String, Map> initConfig(String file) {
        try {
            Map<String, Map> configs = YamlUtils.read(file, LinkedHashMap.class);
            if (configs != null) {
                return configs;
            }
        } catch (Exception e) {
            //读取配置文件出错
        }
        return new HashMap<>();
    }
}
