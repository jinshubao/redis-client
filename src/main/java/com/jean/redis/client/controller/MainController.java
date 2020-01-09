package com.jean.redis.client.controller;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.ctrl.ProgressIndicatorPlaceholder;
import com.jean.redis.client.dialog.CreateRedisServerDialog;
import com.jean.redis.client.dialog.RedisServerInfoDialog;
import com.jean.redis.client.factory.*;
import com.jean.redis.client.handler.RedisDatabaseItemMenuActionHandler;
import com.jean.redis.client.handler.RedisServerItemMenuActionHandler;
import com.jean.redis.client.item.RedisDatabaseItem;
import com.jean.redis.client.item.RedisRootItem;
import com.jean.redis.client.item.RedisServerItem;
import com.jean.redis.client.model.RedisKey;
import com.jean.redis.client.model.RedisServerProperty;
import com.jean.redis.client.model.RedisValue;
import com.jean.redis.client.task.RedisConnectionPoolTask;
import com.jean.redis.client.task.RedisKeysTask;
import com.jean.redis.client.task.RedisServerInfoTask;
import com.jean.redis.client.task.RedisValueTask;
import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.commons.pool2.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

/**
 * @author jinshubao
 */
@SuppressWarnings("unchecked")
public class MainController implements Initializable, AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML
    public TextField keywordTextFiled;
    @FXML
    public Button searchButton;
    @FXML
    public TreeView<Object> serverTreeView;
    @FXML
    public TableView<RedisKey> keyTableView;
    @FXML
    public TableColumn keyNoColumn;
    @FXML
    public TableColumn<RedisKey, byte[]> keyColumn;
    @FXML
    public TableColumn<RedisKey, String> typeColumn;
    @FXML
    public TableColumn<RedisKey, Number> sizeColumn;
    @FXML
    public TableColumn<RedisKey, Number> ttlColumn;
    @FXML
    public TreeTableView treeTableView;
    @FXML
    public ListView<byte[]> valueListView;
    @FXML
    private TextArea valueTextArea;
    @FXML
    public Label messageLabel;
    @FXML
    public SplitPane splitPane;
    @FXML
    public Label serverInfoLabel;
    @FXML
    public MenuItem newConnectionMenuItem;
    @FXML
    public MenuItem aboutMenuItem;

    private final ExecutorService executorService;

    private RedisTreeCellFactory redisTreeCellFactory;
    private RedisKeyTableRowFactory redisKeyTableRowFactory;
    private RedisKeyTableKeyColumnCellFactory redisKeyTableKeyColumnCellFactory;
    private RedisValueListCellFactory redisValueListCellFactory;
    private TableIndexColumnCellFactory tableIndexColumnCellFactory;

    private RedisRootItem serverTreeViewRootItem;

    private ProgressIndicatorPlaceholder keyProgressIndicator;
    private ProgressIndicatorPlaceholder valueProgressIndicator;

    private RedisServerItemMenuActionHandler openConnectionActionEventHandler;
    private RedisServerItemMenuActionHandler closeConnectionActionEventHandler;
    private RedisServerItemMenuActionHandler deleteConnectionActionEventHandler;
    private RedisServerItemMenuActionHandler connectionPropertyActionEventHandler;

    private RedisDatabaseItemMenuActionHandler refreshDatabaseActionEventHandler;
    private RedisDatabaseItemMenuActionHandler flushDatabaseActionEventHandler;

    private EventHandler<ActionEvent> newConnectionActionEventHandler;
    private EventHandler<ActionEvent> aboutActionEventHandler;
    private EventHandler<ActionEvent> searchActionEventHandler;

    private EventHandler<WorkerStateEvent> keyTaskSuccessEventHandler;
    private EventHandler<WorkerStateEvent> keyTaskScheduledEventHandler;

    private EventHandler<WorkerStateEvent> valueTaskScheduledEventHandler;
    private EventHandler<WorkerStateEvent> valueTaskSuccessEventHandler;

    private StringProperty server = new SimpleStringProperty(this, "server");
    private ObjectProperty<Integer> database = new SimpleObjectProperty<>(this, "database");


    public MainController(ExecutorService executorService) {
        this.executorService = executorService;
        logger.debug("main controller constructed...");
    }

    /**
     * @param url    url
     * @param bundle bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        this.initializeFactory();
        this.initializeActionEventHandler();
        this.initializeWorkerStateEventHandler();
        this.initializeProgressIndicator();
        this.initializeMenuBar();
        this.initializeSearch();
        this.initializeTreeView();
        this.initializeTreeTableView();
        this.initializeKeyTableView();
        this.initializeValueListView();
        this.initializeTaskBar();
    }

    private void initializeFactory() {
        this.redisTreeCellFactory = new RedisTreeCellFactory();
        this.redisKeyTableRowFactory = new RedisKeyTableRowFactory();
        this.redisKeyTableKeyColumnCellFactory = new RedisKeyTableKeyColumnCellFactory();
        this.redisValueListCellFactory = new RedisValueListCellFactory();
        tableIndexColumnCellFactory = new TableIndexColumnCellFactory();
    }

    /**
     * 初始化事件处理器
     */
    private void initializeActionEventHandler() {
        //新建连接
        //noinspection CodeBlock2Expr
        newConnectionActionEventHandler = (event -> {
            connectionDialog().showAndWait().ifPresent(serverProperty -> {
                RedisServerItem serverItem = new RedisServerItem(serverProperty,
                        openConnectionActionEventHandler, closeConnectionActionEventHandler,
                        deleteConnectionActionEventHandler, connectionPropertyActionEventHandler);
                serverItem.setExpanded(false);
                serverTreeViewRootItem.getChildren().add(serverItem);
            });
        });
        openConnectionActionEventHandler = (treeItem, menuItem, serverProperty) -> this.openConnection(treeItem, serverProperty);
        closeConnectionActionEventHandler = (treeItem, menuItem, serverProperty) -> this.closeConnectionPool(treeItem, serverProperty);
        connectionPropertyActionEventHandler = (treeItem, menuItem, server) -> this.serverInfo(server);
        deleteConnectionActionEventHandler = (treeItem, menuItem, serverProperty) -> {
            this.closeConnectionPool(treeItem, serverProperty);
            treeItem.getParent().getChildren().remove(treeItem);
        };
        aboutActionEventHandler = (event -> {

        });
        searchActionEventHandler = (event -> {

        });

        refreshDatabaseActionEventHandler = (treeItem, menuItem, serverProperty, database) -> {
            RedisKeysTask task = startRefreshKeyTask(serverProperty, database);
            menuItem.disableProperty().unbind();
            menuItem.disableProperty().bind(task.runningProperty());
        };
        flushDatabaseActionEventHandler = (treeItem, menuItem, serverProperty, database) -> {
            RedisKeysTask task = startRefreshKeyTask(serverProperty, database);
            menuItem.disableProperty().unbind();
            menuItem.disableProperty().bind(task.runningProperty());
        };
    }

    /**
     * 初始化异步任务事件处理器
     */
    private void initializeWorkerStateEventHandler() {
        //redis key task scheduled event handler
        keyTaskScheduledEventHandler = (event -> {
            keyTableView.getItems().clear();
            keyProgressIndicator.indicatorProgressProperty().unbind();
            keyProgressIndicator.indicatorProgressProperty().bind(event.getSource().progressProperty());
            keyProgressIndicator.indicatorVisibleProperty().unbind();
            keyProgressIndicator.indicatorVisibleProperty().bind(event.getSource().runningProperty());
        });
        //redis key task scheduled event handler
        keyTaskSuccessEventHandler = (event -> {
            List<RedisKey> value = (List<RedisKey>) event.getSource().getValue();
            if (value != null) {
                keyTableView.getItems().addAll(value);
            }
        });
        //redis value task success event handler
        valueTaskScheduledEventHandler = (event -> {
            valueListView.getItems().clear();
            valueProgressIndicator.indicatorProgressProperty().unbind();
            valueProgressIndicator.indicatorProgressProperty().bind(event.getSource().progressProperty());
            valueProgressIndicator.indicatorVisibleProperty().unbind();
            valueProgressIndicator.indicatorVisibleProperty().bind(event.getSource().runningProperty());
        });
        //redis value task success event handler
        valueTaskSuccessEventHandler = (event -> {
            RedisValue<?> value = (RedisValue<?>) event.getSource().getValue();
            if (value != null) {
                valueListView.getItems().addAll(value.toList());
            }
        });
    }


    /**
     * 初始化菜单栏
     */
    private void initializeMenuBar() {
        newConnectionMenuItem.setOnAction(newConnectionActionEventHandler);
        aboutMenuItem.setOnAction(aboutActionEventHandler);
    }

    /**
     * 初始化搜索功能
     */
    private void initializeSearch() {
        searchButton.setOnAction(searchActionEventHandler);
    }

    /**
     * 初始化进度条
     */
    private void initializeProgressIndicator() {
        keyProgressIndicator = new ProgressIndicatorPlaceholder();
        valueProgressIndicator = new ProgressIndicatorPlaceholder();
    }

    /**
     * 初始化左侧树形结构
     */
    private void initializeTreeView() {
        serverTreeViewRootItem = new RedisRootItem("服务器列表", newConnectionActionEventHandler);
        serverTreeViewRootItem.setExpanded(true);

        serverTreeView.setCellFactory(redisTreeCellFactory);
        serverTreeView.setRoot(serverTreeViewRootItem);

        //切换数据库
        serverTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue instanceof RedisServerItem) {
                    this.server.set(newValue.getValue().toString());
                    this.database.set(null);
                } else if (newValue instanceof RedisDatabaseItem) {
                    RedisDatabaseItem value = (RedisDatabaseItem) newValue;
                    RedisServerProperty serverProperty = value.getServerProperty();
                    int database = value.getDatabase();
                    this.server.set(serverProperty.toString());
                    this.database.set(database);
                    startRefreshKeyTask(serverProperty, database);
                } else {
                    server.set(null);
                    database.set(null);
                }
            } else {
                server.set(null);
                database.set(null);
            }
        });
    }

    private void initializeTreeTableView() {
    }


    /**
     * 初始化 redis key 表格
     */
    private void initializeKeyTableView() {
        keyNoColumn.setCellFactory(tableIndexColumnCellFactory);
        keyColumn.setCellFactory(redisKeyTableKeyColumnCellFactory);
        keyColumn.setCellValueFactory(param -> param.getValue().keyProperty());
        typeColumn.setCellValueFactory(param -> param.getValue().typeProperty());
        sizeColumn.setCellValueFactory(param -> param.getValue().sizeProperty());
        ttlColumn.setCellValueFactory(param -> param.getValue().ttlProperty());
        keyTableView.setPlaceholder(keyProgressIndicator);
        keyTableView.setRowFactory(redisKeyTableRowFactory);
        keyTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                startRefreshValueTask(newValue.getServer(), newValue.getDatabase(), newValue.getKey());
            }
        });
    }

    /**
     * 初始化 redis value 列表
     */
    private void initializeValueListView() {
        valueListView.setCellFactory(redisValueListCellFactory);
        valueListView.setPlaceholder(valueProgressIndicator);
        valueListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        valueTextArea.setWrapText(true);
        valueTextArea.setEditable(false);

        StringBinding stringBinding = Bindings.createStringBinding(() -> {
            byte[] item = valueListView.getSelectionModel().getSelectedItem();
            return item == null ? null : new String(item, CommonConstant.CHARSET_UTF8);
        }, valueListView.getSelectionModel().selectedItemProperty());
        valueTextArea.textProperty().bind(stringBinding);
    }

    private void initializeTaskBar() {
        StringBinding stringBinding = Bindings.createStringBinding(() -> {
            if (server.get() != null && database.get() != null) {
                return server.get() + ":" + database.get();
            } else {
                if (server.get() != null) {
                    return server.get();
                } else if (database.get() != null) {
                    return database.get().toString();
                } else {
                    return null;
                }
            }
        }, server, database);
        serverInfoLabel.textProperty().bind(stringBinding);
    }

    private CreateRedisServerDialog connectionDialog() {
        RedisServerProperty property = new RedisServerProperty();
        property.setHost("101.132.156.127");
        property.setPort(6379);
        property.setPassword("123!=-09][po");
        return CreateRedisServerDialog.newInstance(property);
    }

    private List<Task> keyTaskList = new ArrayList();
    private List<Task> valueTaskList = new ArrayList();

    /**
     * 启动获取 redis key 的后台任务
     *
     * @param serverProperty server
     * @param database       database
     * @return 返回任务
     */
    @SuppressWarnings("Duplicates")
    private RedisKeysTask startRefreshKeyTask(RedisServerProperty serverProperty, int database) {
        keyTaskList.forEach(item -> item.cancel(false));
        keyTaskList.clear();
        RedisKeysTask task = RedisTaskFactory.createKeyTask(serverProperty, database);
        task.setOnScheduled(keyTaskScheduledEventHandler);
        task.setOnSucceeded(keyTaskSuccessEventHandler);
        executorService.execute(task);
        keyTaskList.add(task);
        return task;
    }

    /**
     * 启动获取 redis value 的后台任务
     *
     * @param serverProperty server
     * @param database       database
     * @param key            key
     */
    @SuppressWarnings("Duplicates")
    private void startRefreshValueTask(RedisServerProperty serverProperty, int database, byte[] key) {
        valueTaskList.forEach(item -> item.cancel(false));
        valueTaskList.clear();
        RedisValueTask task = RedisTaskFactory.createValueTask(serverProperty, database, key);
        task.setOnScheduled(valueTaskScheduledEventHandler);
        task.setOnSucceeded(valueTaskSuccessEventHandler);
        executorService.execute(task);
        valueTaskList.add(task);
    }


    /**
     * 初始化连接池
     *
     * @param serverItem     treeItem
     * @param serverProperty server
     */
    private void openConnection(TreeItem serverItem, RedisServerProperty serverProperty) {
        if (serverItem.isExpanded()) {
            return;
        }
        RedisConnectionPoolTask task = RedisTaskFactory.createRedisPoolTask(serverProperty);
        task.setOnSucceeded(event -> {
            for (int i = 0; i < 16; i++) {
                TreeItem item = new RedisDatabaseItem(serverProperty, i, refreshDatabaseActionEventHandler, flushDatabaseActionEventHandler);
                serverItem.getChildren().add(item);
            }
            serverItem.setExpanded(true);
            CommonConstant.putConnectionPool(serverProperty.getUuid(), task.getValue());
        });
        executorService.execute(task);
    }

    private void closeConnectionPool(TreeItem serverItem, RedisServerProperty serverProperty) {
        if (!serverItem.isExpanded()) {
            return;
        }
        keyTableView.getItems().clear();
        valueListView.getItems().clear();
        ObjectPool<StatefulRedisConnection<byte[], byte[]>> pool = CommonConstant.removeConnectionPool(serverProperty.getUuid());
        pool.close();
        serverItem.getChildren().clear();
        serverItem.setExpanded(false);
    }


    private void serverInfo(RedisServerProperty serverProperty) {
        RedisServerInfoTask task = RedisTaskFactory.createServerInfoTask(serverProperty);
        RedisServerInfoDialog dialog = RedisServerInfoDialog.newInstance();
        dialog.getTextArea().textProperty().bind(task.valueProperty());
        executorService.execute(task);
        dialog.show();
    }


    /**
     * 主程序退出回调
     */
    @Override
    public void close() {
        logger.debug("main controller destroy...");
        CommonConstant.GLOBAL_REDIS_CONNECTION_POOL_CACHE.values().forEach(ObjectPool::close);
        CommonConstant.GLOBAL_REDIS_CLIENT_CACHE.values().forEach(AbstractRedisClient::shutdownAsync);
        executorService.shutdown();
    }
}
