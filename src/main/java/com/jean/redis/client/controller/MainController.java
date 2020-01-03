package com.jean.redis.client.controller;

import com.jean.redis.client.Service.RedisKeyListService;
import com.jean.redis.client.Service.RedisServerInfoService;
import com.jean.redis.client.Service.RedisValueService;
import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.entry.Node;
import com.jean.redis.client.factory.RedisKeyTableKeyColumnCellFactory;
import com.jean.redis.client.factory.RedisKeyTableRowFactory;
import com.jean.redis.client.factory.RedisNodeTreeCellFactory;
import com.jean.redis.client.factory.RedisValueListCellFactory;
import com.jean.redis.client.model.*;
import io.lettuce.core.RedisClient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author jinshubao
 */
@Controller
@SuppressWarnings("unchecked")
public class MainController implements Initializable, InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML
    public TextField keyword;
    @FXML
    public Button search;
    @FXML
    public TreeView<Node> tree;
    @FXML
    public TableView<RedisKey> table;
    @FXML
    public TableColumn<RedisKey, byte[]> keyColumn;
    @FXML
    public TableColumn<RedisKey, String> typeColumn;
    @FXML
    public TableColumn<RedisKey, Number> sizeColumn;
    @FXML
    public TableColumn<RedisKey, Number> ttlColumn;
    @FXML
    public ListView<byte[]> detail;
    @FXML
    public Label message;
    @FXML
    public SplitPane splitPane;
    @FXML
    public Label dbInfo;
    @FXML
    public MenuItem newConnection;
    @FXML
    public MenuItem about;

    @Autowired
    private RedisServerInfoService redisServerInfoService;

    private final RedisKeyListService redisKeyListService;

    private final RedisValueService redisValueService;

    private final RedisKeyTableRowFactory redisKeyTableRowFactory;

    private final RedisKeyTableKeyColumnCellFactory redisKeyTableKeyColumnCellFactory;

    private final RedisValueListCellFactory redisValueListCellFactory;

    public MainController(RedisKeyListService redisKeyListService,
                          RedisValueService redisValueService,
                          RedisKeyTableRowFactory redisKeyTableRowFactory,
                          RedisKeyTableKeyColumnCellFactory redisKeyTableKeyColumnCellFactory,
                          RedisValueListCellFactory redisValueListCellFactory) {
        this.redisKeyListService = redisKeyListService;
        this.redisValueService = redisValueService;
        this.redisKeyTableRowFactory = redisKeyTableRowFactory;
        this.redisKeyTableKeyColumnCellFactory = redisKeyTableKeyColumnCellFactory;
        this.redisValueListCellFactory = redisValueListCellFactory;
        logger.debug("main controller constructed...");
    }

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

        about.setOnAction(event -> {

        });
        search.disableProperty().bind(redisKeyListService.runningProperty().or(redisValueService.runningProperty()));
        search.setOnAction(event -> {
            Node value = tree.getSelectionModel().getSelectedItem().getValue();
            if (value instanceof HostNode) {
                redisServerInfoService.restart(((HostNode) value).getConfig());
            }
        });

        tree.setCellFactory(new RedisNodeTreeCellFactory());
        tree.setRoot(new TreeItem<>(new RootNode("服务器列表")));
        tree.getRoot().setExpanded(true);

        //切换数据库
        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            detail.getItems().clear();
            if (newValue != null) {
                Node model = newValue.getValue();
                dbInfo.setText(model.toString());
                if (model instanceof DBNode) {
                    redisKeyListService.restart(((DBNode) model).getConfig(), "*", 100L);
                }
            } else {
                dbInfo.setText(null);
            }
        });

        keyColumn.setCellFactory(redisKeyTableKeyColumnCellFactory);
        keyColumn.setCellValueFactory(param -> param.getValue().keyProperty());
        typeColumn.setCellValueFactory(param -> param.getValue().typeProperty());
        sizeColumn.setCellValueFactory(param -> param.getValue().sizeProperty());
        ttlColumn.setCellValueFactory(param -> param.getValue().ttlProperty());
        table.setRowFactory(redisKeyTableRowFactory);

        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                redisValueService.restart(newValue.getConfig(), newValue.getKey());
            }
        });
        ProgressIndicator listProgress = new ProgressIndicator(-1D);
        listProgress.setMaxHeight(50D);
        listProgress.setMaxWidth(50D);
        listProgress.visibleProperty().bind(redisKeyListService.runningProperty());
        table.setPlaceholder(listProgress);

        ProgressIndicator detailProgress = new ProgressIndicator(-1D);
        detailProgress.setMaxHeight(50D);
        detailProgress.setMaxWidth(50D);
        detailProgress.visibleProperty().bind(redisValueService.runningProperty());

        detail.setCellFactory(redisValueListCellFactory);
        detail.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        detail.setPlaceholder(detailProgress);

        redisKeyListService.setOnScheduled(event -> {
            table.getItems().clear();
            message.textProperty().unbind();
            message.textProperty().bind(redisKeyListService.messageProperty());
        });
        redisKeyListService.setOnSucceeded(event -> {
            if (!table.getItems().isEmpty()) {
                table.getItems().clear();
            }
            List<RedisKey> value = redisKeyListService.getValue();
            table.getItems().addAll(value);
        });

        redisValueService.setOnScheduled(event -> {
            detail.getItems().clear();
            message.textProperty().unbind();
            message.textProperty().bind(redisValueService.messageProperty());
        });

        redisValueService.setOnSucceeded(event -> {
            if (!detail.getItems().isEmpty()) {
                detail.getItems().clear();
            }
            RedisValue value = redisValueService.getValue();
            detail.getItems().addAll(value.toList());
        });
    }


    /**
     * 初始化
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug("main controller afterPropertiesSet...");
    }

    /**
     * 主程序退出回调
     */
    @Override
    public void destroy() throws Exception {
        logger.debug("main controller destroy...");
        CommonConstant.REDIS_CLIENT_MAP.values().forEach(RedisClient::shutdown);
    }

}
