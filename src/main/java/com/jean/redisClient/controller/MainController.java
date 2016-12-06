package com.jean.redisClient.controller;

import com.jean.redisClient.Service.DelService;
import com.jean.redisClient.Service.DetailService;
import com.jean.redisClient.Service.ListService;
import com.jean.redisClient.constant.CommonConstant;
import com.jean.redisClient.factory.ListCellFactory;
import com.jean.redisClient.factory.TableCellFactory;
import com.jean.redisClient.factory.TreeCellFactory;
import com.jean.redisClient.model.*;
import com.jean.redisClient.utils.TreeItemUtils;
import com.jean.redisClient.utils.YamlUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.FileNotFoundException;
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
    public Label message;
    @FXML
    public SplitPane splitPane;
    @FXML
    public Label dbInfo;

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
            dbChange(newValue);
        });

        table.getColumns().get(0).setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getKey()));
        table.getColumns().get(1).setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getType()));
        table.getColumns().get(2).setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getSize()));
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            detailService.addParams("item", newValue);
            detailService.restart(newValue);
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
            List<ListModel> list = (List<ListModel>) listService.getValue();
            table.getItems().addAll(list);
            //生成树形结构
            TreeItemUtils.generateDir(listService.getTreeItem(), new ArrayList(table.getItems()));
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
        Map config = initConfig(CommonConstant.CONFIG_FILE_PATH + CommonConstant.CONFIG_FILE_NAME);
        config.forEach((key, m) -> {
            Map map = (Map) m;
            Object auth = map.get("auth");
            Object hostName = map.get("hostName");
            Object port = map.get("port");
            HostModel hostModel = new HostModel("unnamedhost", 0);
            if (hostName != null) {
                hostModel.setHostName(hostName.toString().trim());
            }
            if (port != null && !port.toString().trim().isEmpty()) {
                hostModel.setPort(Integer.parseInt(port.toString().trim()));
            }
            if (auth != null) {
                hostModel.setAuth(auth.toString());
            }
            tree.getRoot().getChildren().add(TreeItemUtils.createHostTreeItem(hostModel));
        });
    }

    /**
     * 切换db
     */
    private void dbChange(TreeItem<NodeModel> newValue) {
        if (newValue == null) {
            dbInfo.setText(null);
            return;
        }
        NodeModel model = newValue.getValue();
        dbInfo.setText(model.location());
        if (model instanceof DbModel) {
            listService.restart(newValue);
        }
    }

    //主程序退出回调
    public void close() throws FileNotFoundException {
        //保存配置
        Map configs = new LinkedHashMap<>();
        ObservableList<TreeItem<NodeModel>> treeItems = tree.getRoot().getChildren();
        for (int i = 0; i < treeItems.size(); i++) {
            HostModel value = (HostModel) treeItems.get(i).getValue();
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
            Map<String, Map> configs = new LinkedHashMap<>();
            configs = YamlUtils.read(file, configs.getClass());
            if (configs != null) {
                return configs;
            }
        } catch (Exception e) {
            //读取配置文件出错
        }
        return new HashMap<>();
    }
}
