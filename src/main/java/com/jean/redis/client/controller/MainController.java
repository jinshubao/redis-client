package com.jean.redis.client.controller;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.factory.*;
import com.jean.redis.client.mange.TaskManger;
import com.jean.redis.client.model.RedisKey;
import com.jean.redis.client.model.RedisValue;
import com.jean.redis.client.util.MessageUtils;
import com.jean.redis.client.view.ProgressIndicatorPlaceholder;
import com.jean.redis.client.view.RedisRootItem;
import com.jean.redis.client.view.action.IMouseAction;
import com.jean.redis.client.view.handler.IMenuBarActionEventHandler;
import com.jean.redis.client.view.handler.impl.MenuBarActionEventHandlerImpl;
import com.jean.redis.client.view.handler.impl.RedisKeyActionEventHandlerImpl;
import com.jean.redis.client.view.handler.impl.RedisRootItemActionEventHandler;
import com.jean.redis.client.view.handler.impl.RedisValueActionEventHandlerImpl;
import io.lettuce.core.AbstractRedisClient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.apache.commons.pool2.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author jinshubao
 */
@SuppressWarnings("unchecked")
public class MainController implements Initializable, AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML
    private BorderPane root;
    @FXML
    private Button searchButton;
    @FXML
    private TreeView<Object> serverTreeView;
    @FXML
    private TableView<RedisKey> keyTableView;
    @FXML
    private TableColumn<RedisKey, Integer> keyNoColumn;
    @FXML
    private TableColumn<RedisKey, byte[]> keyColumn;
    @FXML
    private TableColumn<RedisKey, String> typeColumn;
    @FXML
    private TableColumn<RedisKey, Number> sizeColumn;
    @FXML
    private TableColumn<RedisKey, Number> ttlColumn;
    @FXML
    private TableView<RedisValue> valueTableView;
    @FXML
    private TableColumn<RedisValue, Integer> valueNoColumn;
    @FXML
    private TableColumn<RedisValue, byte[]> valueKeyColumn;
    @FXML
    private TableColumn<RedisValue, byte[]> valueColumn;
    @FXML
    private TableColumn<RedisValue, Number> valueScoreColumn;

    @FXML
    private Label messageLabel;
    @FXML
    private MenuItem exit;
    @FXML
    private MenuItem aboutMenuItem;

    /**
     * @param url    url
     * @param bundle bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle bundle) {

        IMenuBarActionEventHandler menuBarActionEventHandler = new MenuBarActionEventHandlerImpl(root);
        exit.setOnAction(event -> menuBarActionEventHandler.exit());
        aboutMenuItem.setOnAction(event -> menuBarActionEventHandler.about());

        RedisRootItem rootItem = new RedisRootItem("服务器列表", new RedisRootItemActionEventHandler(root));
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
        keyTableView.setRowFactory(new RedisKeyTableRowFactory(new RedisKeyActionEventHandlerImpl(root)));

        valueNoColumn.setCellFactory(tableViewRowIndexColumnCellFactory);
        valueScoreColumn.setCellValueFactory(param -> param.getValue().scoreProperty());
        valueKeyColumn.setCellFactory(tableViewByteColumnCellFactory);
        valueKeyColumn.setCellValueFactory(param -> param.getValue().keyProperty());
        valueColumn.setCellFactory(tableViewByteColumnCellFactory);
        valueColumn.setCellValueFactory(param -> param.getValue().valueProperty());
        valueTableView.setPlaceholder(new ProgressIndicatorPlaceholder());
        valueTableView.setRowFactory(new RedisValueTableRowFactory(new RedisValueActionEventHandlerImpl(root)));
        MessageUtils.init(messageLabel);
    }

    /**
     * 主程序退出回调
     */
    @Override
    public void close() {
        logger.debug("main controller destroy...");
        CommonConstant.GLOBAL_REDIS_CONNECTION_POOL_CACHE.values().forEach(ObjectPool::close);
        CommonConstant.GLOBAL_REDIS_CLIENT_CACHE.values().forEach(AbstractRedisClient::shutdownAsync);
        TaskManger.getInstance().shutdown();
    }

}
