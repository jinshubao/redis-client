package com.jean.redis.client.controller;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.ctrl.ProgressIndicatorPlaceholder;
import com.jean.redis.client.dialog.CreateRedisServerDialog;
import com.jean.redis.client.dialog.RedisServerInfoDialog;
import com.jean.redis.client.factory.RedisKeyTableKeyColumnCellFactory;
import com.jean.redis.client.factory.RedisKeyTableRowFactory;
import com.jean.redis.client.factory.RedisTreeCellFactory;
import com.jean.redis.client.factory.TableIndexColumnCellFactory;
import com.jean.redis.client.handler.*;
import com.jean.redis.client.item.RedisDatabaseItem;
import com.jean.redis.client.item.RedisRootItem;
import com.jean.redis.client.item.RedisServerItem;
import com.jean.redis.client.model.RedisKey;
import com.jean.redis.client.model.RedisServerProperty;
import com.jean.redis.client.model.RedisValue;
import com.jean.redis.client.model.RedisValueWrapper;
import com.jean.redis.client.task.RedisConnectionPoolTask;
import com.jean.redis.client.task.RedisKeysTask;
import com.jean.redis.client.task.RedisServerInfoTask;
import com.jean.redis.client.task.RedisValueTask;
import com.jean.redis.client.util.MouseEventUtils;
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
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.apache.commons.pool2.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
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
    public TableColumn<Object, Object> keyNoColumn;
    @FXML
    public TableColumn<RedisKey, byte[]> keyColumn;
    @FXML
    public TableColumn<RedisKey, String> typeColumn;
    @FXML
    public TableColumn<RedisKey, Number> sizeColumn;
    @FXML
    public TableColumn<RedisKey, Number> ttlColumn;
    @FXML
    public TableView<RedisValue> valueTableView;
    @FXML
    public TableColumn<Object, Object> valueNoColumn;
    @FXML
    public TableColumn<RedisValue, byte[]> valueKeyColumn;
    @FXML
    public TableColumn<RedisValue, byte[]> valueColumn;
    @FXML
    public TableColumn<RedisValue, Number> valueScoreColumn;
    @FXML
    public SplitPane valueSplitPane;
    @FXML
    private TextField keyTextFiled;
    @FXML
    private TextArea valueTextArea;
    @FXML
    private Button saveButton;
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
    private TableIndexColumnCellFactory tableIndexColumnCellFactory;

    private ProgressIndicatorPlaceholder keyProgressIndicator;
    private ProgressIndicatorPlaceholder valueProgressIndicator;

    private MenuBarActionEventHandler menuBarActionEventHandler;

    private RedisRootItemActionEventHandler redisRootItemActionEventHandler;
    private RedisServerItemActionEventHandler redisServerItemActionEventHandler;
    private RedisDatabaseItemActionEventHandler redisDatabaseItemActionEventHandler;

    private RedisKeyActionEventHandler redisKeyActionEventHandler;

    private EventHandler<WorkerStateEvent> keyTaskWorkerStateEventHandler;

    private EventHandler<WorkerStateEvent> valueTaskWorkerStateEventHandler;

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
        this.initializeActionEventHandler();
        this.initializeFactory();
        this.initializeWorkerStateEventHandler();
        this.initializeProgressIndicator();
        this.initializeMenuBar();
        this.initializeSearch();
        this.initializeTreeView();
        this.initializeKeyTableView();
        this.initializeValueListView();
        this.initializeTaskBar();
    }


    /**
     * 初始化事件处理器
     */
    private void initializeActionEventHandler() {
        menuBarActionEventHandler = new MenuBarActionEventHandlerImpl();
        redisRootItemActionEventHandler = new RedisRootItemActionEventHandlerImpl();
        redisServerItemActionEventHandler = new RedisServerItemActionEventHandlerImpl();
        redisDatabaseItemActionEventHandler = new RedisDatabaseItemActionEventHandlerImpl();
        redisKeyActionEventHandler = new RedisKeyActionEventHandlerImpl(CommonConstant.CHARSET_UTF8);
    }

    private void initializeFactory() {
        this.redisTreeCellFactory = new RedisTreeCellFactory();
        this.redisKeyTableRowFactory = new RedisKeyTableRowFactory(redisKeyActionEventHandler);
        this.redisKeyTableKeyColumnCellFactory = new RedisKeyTableKeyColumnCellFactory(CommonConstant.CHARSET_UTF8);
        tableIndexColumnCellFactory = new TableIndexColumnCellFactory();
    }


    /**
     * 初始化异步任务事件处理器
     */
    private void initializeWorkerStateEventHandler() {
        //redis key task scheduled event handler
        keyTaskWorkerStateEventHandler = (event -> {
            if (event.getEventType() == WorkerStateEvent.WORKER_STATE_SCHEDULED) {
                keyTableView.getItems().clear();
                keyProgressIndicator.indicatorProgressProperty().unbind();
                keyProgressIndicator.indicatorProgressProperty().bind(event.getSource().progressProperty());
                keyProgressIndicator.indicatorVisibleProperty().unbind();
                keyProgressIndicator.indicatorVisibleProperty().bind(event.getSource().runningProperty());
            } else if (event.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
                List<RedisKey> value = (List<RedisKey>) event.getSource().getValue();
                if (value != null) {
                    keyTableView.getItems().addAll(value);
                }
            }
        });

        //redis value task success event handler
        valueTaskWorkerStateEventHandler = (event -> {
            if (event.getEventType() == WorkerStateEvent.WORKER_STATE_SCHEDULED) {
                valueTableView.getItems().clear();
                valueProgressIndicator.indicatorProgressProperty().unbind();
                valueProgressIndicator.indicatorProgressProperty().bind(event.getSource().progressProperty());
                valueProgressIndicator.indicatorVisibleProperty().unbind();
                valueProgressIndicator.indicatorVisibleProperty().bind(event.getSource().runningProperty());
            } else if (event.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
                RedisValueWrapper value = (RedisValueWrapper) event.getSource().getValue();
                if (value != null) {
                    if (CommonConstant.KeyType.STRING.equalsIgnoreCase(value.getType())) {
                        valueSplitPane.getItems().remove(valueTableView);
                    } else {
                        if (!valueSplitPane.getItems().contains(valueTableView)) {
                            valueSplitPane.getItems().add(0, valueTableView);
                        }
                    }
                    valueKeyColumn.setVisible(CommonConstant.KeyType.HASH.equalsIgnoreCase(value.getType()));
                    valueScoreColumn.setVisible(CommonConstant.KeyType.ZSET.equalsIgnoreCase(value.getType()));
                    if (CommonConstant.KeyType.STRING.equalsIgnoreCase(value.getType())) {
                        updateKeyValueText(value.getKey(), value.getValues().get(0).getValue());
                    } else {
                        updateKeyValueText(null, null);
                        valueTableView.getItems().addAll(value.getValues());
                    }
                }
            }
        });
    }


    /**
     * 初始化菜单栏
     */
    private void initializeMenuBar() {
        newConnectionMenuItem.setOnAction(event -> menuBarActionEventHandler.create(event));
        aboutMenuItem.setOnAction(event -> menuBarActionEventHandler.about(event));
    }

    /**
     * 初始化搜索功能
     */
    private void initializeSearch() {
        searchButton.setOnAction(event -> {

        });
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
        RedisRootItem rootItem = new RedisRootItem("服务器列表", redisRootItemActionEventHandler);
        rootItem.setExpanded(true);

        serverTreeView.setCellFactory(redisTreeCellFactory);
        serverTreeView.setRoot(rootItem);

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
        keyTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, redisKey) -> {
            if (redisKey != null) {
                startRefreshValueTask(redisKey.getServer(), redisKey.getDatabase(), redisKey.getKey());
            }
        });

    }

    /**
     * 初始化 redis value 列表
     */
    private void initializeValueListView() {

        Callback<TableColumn<RedisValue, byte[]>, TableCell<RedisValue, byte[]>> cellCallback = new Callback<TableColumn<RedisValue, byte[]>, TableCell<RedisValue, byte[]>>() {
            @Override
            public TableCell<RedisValue, byte[]> call(TableColumn<RedisValue, byte[]> param) {
                return new TableCell<RedisValue, byte[]>() {
                    @Override
                    protected void updateItem(byte[] item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(new String(item, CommonConstant.CHARSET_UTF8));
                        }
                    }
                };
            }
        };

        valueTextArea.setWrapText(true);

        valueNoColumn.setCellFactory(tableIndexColumnCellFactory);

        valueScoreColumn.setCellValueFactory(param -> param.getValue().scoreProperty());

        valueKeyColumn.setCellFactory(cellCallback);
        valueKeyColumn.setCellValueFactory(param -> param.getValue().keyProperty());

        valueColumn.setCellFactory(cellCallback);
        valueColumn.setCellValueFactory(param -> param.getValue().valueProperty());

        valueTableView.setPlaceholder(valueProgressIndicator);

        valueTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateKeyValueText(newValue.getKey(), newValue.getValue());
            } else {
                updateKeyValueText(null, null);
            }
        });
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

    private void updateKeyValueText(byte[] key, byte[] value) {
        if (key != null) {
            keyTextFiled.setText(new String(key, CommonConstant.CHARSET_UTF8));
        } else {
            keyTextFiled.setText(null);
        }
        if (value != null) {
            valueTextArea.setText(new String(value, CommonConstant.CHARSET_UTF8));
        } else {
            valueTextArea.setText(null);
        }
    }


    private List<Task<?>> keyTaskList = new ArrayList<>();
    private List<Task<?>> valueTaskList = new ArrayList<>();

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
        RedisKeysTask task = new RedisKeysTask(serverProperty, database, CommonConstant.CHARSET_UTF8);
        task.setOnScheduled(keyTaskWorkerStateEventHandler);
        task.setOnSucceeded(keyTaskWorkerStateEventHandler);
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
        RedisValueTask task = new RedisValueTask(serverProperty, database, key);
        task.setOnScheduled(valueTaskWorkerStateEventHandler);
        task.setOnSucceeded(valueTaskWorkerStateEventHandler);
        executorService.execute(task);
        valueTaskList.add(task);
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

    private void addNewServer() {
        connectionDialog().showAndWait().ifPresent(property -> {
            RedisServerItem serverItem = new RedisServerItem(property, redisServerItemActionEventHandler);
            serverItem.setExpanded(false);
            serverTreeView.getRoot().getChildren().add(serverItem);
        });
    }

    private void connectServer(RedisServerItem treeItem, RedisServerProperty serverProperty) {
        RedisConnectionPoolTask task = new RedisConnectionPoolTask(serverProperty);
        task.setOnSucceeded(event -> {
            for (int i = 0; i < 16; i++) {
                RedisDatabaseItem item = new RedisDatabaseItem(serverProperty, i, redisDatabaseItemActionEventHandler);
                treeItem.getChildren().add(item);
            }
            treeItem.setOpen(true);
            treeItem.setExpanded(true);
            CommonConstant.putConnectionPool(serverProperty.getUuid(), task.getValue());
        });
        executorService.execute(task);
    }

    private CreateRedisServerDialog connectionDialog() {
        RedisServerProperty property = new RedisServerProperty();
        return CreateRedisServerDialog.newInstance(property);
    }


    private class MenuBarActionEventHandlerImpl implements MenuBarActionEventHandler {

        @Override
        public void create(ActionEvent actionEvent) {
            addNewServer();
        }

        @Override
        public void exit(ActionEvent actionEvent) {
            System.exit(0);
        }

        @Override
        public void about(ActionEvent actionEvent) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About");
            alert.setHeaderText("Redis客户端工具");
            alert.setContentText("暂不支持集群模式和哨兵模式");
            alert.show();
        }
    }


    private class RedisRootItemActionEventHandlerImpl implements RedisRootItemActionEventHandler {

        @Override
        public void click(MouseEvent mouseEvent, TreeItem<?> treeItem) {
            //
        }

        @Override
        public void create(ActionEvent actionEvent, TreeItem<?> treeItem) {
            addNewServer();
        }
    }

    private class RedisServerItemActionEventHandlerImpl implements RedisServerItemActionEventHandler {

        @Override
        public void click(MouseEvent mouseEvent, RedisServerItem treeItem, RedisServerProperty serverProperty) {
            if (MouseEventUtils.isDoubleClick(mouseEvent) && !treeItem.isOpen()) {
                connectServer(treeItem, serverProperty);
            }
        }

        @Override
        public void open(ActionEvent actionEvent, RedisServerItem treeItem, RedisServerProperty serverProperty) {
            if (treeItem.isOpen()) {
                return;
            }
            connectServer(treeItem, serverProperty);
        }

        @Override
        public void close(ActionEvent event, RedisServerItem treeItem, RedisServerProperty serverProperty) {
            disconnectServer(treeItem, serverProperty);
        }

        @Override
        public void delete(ActionEvent event, RedisServerItem treeItem, RedisServerProperty serverProperty) {
            disconnectServer(treeItem, serverProperty);
            treeItem.getParent().getChildren().remove(treeItem);
        }

        @Override
        public void property(ActionEvent event, RedisServerItem treeItem, RedisServerProperty serverProperty) {
            RedisServerInfoTask task = new RedisServerInfoTask(serverProperty);
            RedisServerInfoDialog dialog = RedisServerInfoDialog.newInstance();
            dialog.getTextArea().textProperty().bind(task.valueProperty());
            executorService.execute(task);
            dialog.show();
        }


        private void disconnectServer(RedisServerItem serverItem, RedisServerProperty serverProperty) {
            if (!serverItem.isOpen()) {
                return;
            }
            keyTableView.getItems().clear();
            valueTableView.getItems().clear();
            updateKeyValueText(null, null);
            ObjectPool<StatefulRedisConnection<byte[], byte[]>> pool = CommonConstant.removeConnectionPool(serverProperty.getUuid());
            pool.close();
            serverItem.getChildren().clear();
            serverItem.setExpanded(false);
            serverItem.setOpen(false);
        }
    }


    private class RedisDatabaseItemActionEventHandlerImpl implements RedisDatabaseItemActionEventHandler {

        @Override
        public void click(MouseEvent mouseEvent, TreeItem<?> treeItem, RedisServerProperty serverProperty, int database) {
            if (MouseEventUtils.isDoubleClick(mouseEvent)) {
                startRefreshKeyTask(serverProperty, database);
            }
        }

        @Override
        public void refresh(ActionEvent actionEvent, TreeItem<?> treeItem, RedisServerProperty serverProperty, int database) {
            RedisKeysTask task = startRefreshKeyTask(serverProperty, database);
            MenuItem menuItem = (MenuItem) actionEvent.getSource();
            menuItem.disableProperty().unbind();
            menuItem.disableProperty().bind(task.runningProperty());
        }

        @Override
        public void flush(ActionEvent actionEvent, TreeItem<?> treeItem, RedisServerProperty serverProperty, int database) {
            //TODO
            this.refresh(actionEvent, treeItem, serverProperty, database);
        }
    }

    private class RedisKeyActionEventHandlerImpl implements RedisKeyActionEventHandler {

        private final Charset charset;

        private RedisKeyActionEventHandlerImpl(Charset charset) {
            this.charset = charset;
        }

        @Override
        public void click(MouseEvent mouseEvent, RedisKey redisKey) {
            if (MouseEventUtils.isDoubleClick(mouseEvent) && redisKey != null) {
                startRefreshValueTask(redisKey.getServer(), redisKey.getDatabase(), redisKey.getKey());
            }
        }

        @Override
        public void copy(ActionEvent actionEvent, RedisKey redisKey) {
            Map<DataFormat, Object> content = new HashMap<>();
            content.put(DataFormat.PLAIN_TEXT, new String(redisKey.getKey(), charset));
            Clipboard.getSystemClipboard().setContent(content);
        }

        @Override
        public void delete(ActionEvent actionEvent, RedisKey redisKey) {
            keyTableView.getItems().remove(redisKey);
        }

    }
}
