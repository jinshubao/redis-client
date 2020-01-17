package com.jean.redis.client.controller;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.ctrl.ProgressIndicatorPlaceholder;
import com.jean.redis.client.dialog.CreateRedisServerDialog;
import com.jean.redis.client.dialog.RedisServerInfoDialog;
import com.jean.redis.client.factory.TableViewByteColumnCellFactory;
import com.jean.redis.client.factory.TableViewRowIndexColumnCellFactory;
import com.jean.redis.client.factory.key.RedisKeyTableRowFactory;
import com.jean.redis.client.factory.server.TreeCellFactory;
import com.jean.redis.client.handler.*;
import com.jean.redis.client.item.RedisDatabaseItem;
import com.jean.redis.client.item.RedisRootItem;
import com.jean.redis.client.item.RedisServerItem;
import com.jean.redis.client.mange.TaskManger;
import com.jean.redis.client.model.*;
import com.jean.redis.client.task.RedisConnectionPoolTask;
import com.jean.redis.client.task.RedisKeysTask;
import com.jean.redis.client.task.RedisServerInfoTask;
import com.jean.redis.client.task.RedisValueTask;
import com.jean.redis.client.util.MessageUtils;
import com.jean.redis.client.util.MouseEventUtils;
import com.jean.redis.client.util.StringUtils;
import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseEvent;
import org.apache.commons.pool2.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
    public TableColumn<RedisKey, Integer> keyNoColumn;
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
    public TableColumn<RedisValue, Integer> valueNoColumn;
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
    @SuppressWarnings("unused")
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

    private TreeCellFactory treeCellFactory;
    private RedisKeyTableRowFactory redisKeyTableRowFactory;
    private TableViewByteColumnCellFactory tableViewByteColumnCellFactory;
    private TableViewRowIndexColumnCellFactory tableViewRowIndexColumnCellFactory;

    private ProgressIndicatorPlaceholder keyProgressIndicator;
    private ProgressIndicatorPlaceholder valueProgressIndicator;

    private MenuBarActionEventHandler menuBarActionEventHandler;

    private RedisRootItemActionEventHandler redisRootItemActionEventHandler;
    private RedisServerItemActionEventHandler redisServerItemActionEventHandler;
    private RedisDatabaseItemActionEventHandler redisDatabaseItemActionEventHandler;

    private RedisKeyActionEventHandler redisKeyActionEventHandler;

    private EventHandler<WorkerStateEvent> keyTaskWorkerStateEventHandler;
    private EventHandler<WorkerStateEvent> valueTaskWorkerStateEventHandler;
    private EventHandler<WorkerStateEvent> serverInfoWorkerStateEventHandler;

    private StringProperty server = new SimpleStringProperty(this, "server");
    private ObjectProperty<Integer> database = new SimpleObjectProperty<>(this, "database");

    public MainController() {
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
        this.initializeMenuBar();
        this.initializeSearch();
        this.initializeTreeView();
        this.initializeKeyTableView();
        this.initializeValueListView();
        this.initializeTaskBar();
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

    /**
     * 初始化事件处理器
     */
    private void initializeActionEventHandler() {
        menuBarActionEventHandler = new MenuBarActionEventHandlerImpl();
        redisRootItemActionEventHandler = new RedisRootItemActionEventHandlerImpl();
        redisServerItemActionEventHandler = new RedisServerItemActionEventHandlerImpl();
        redisDatabaseItemActionEventHandler = new RedisDatabaseItemActionEventHandlerImpl();
        redisKeyActionEventHandler = new RedisKeyActionEventHandlerImpl();
    }

    private void initializeFactory() {
        this.treeCellFactory = new TreeCellFactory();
        this.redisKeyTableRowFactory = new RedisKeyTableRowFactory(redisKeyActionEventHandler);
        this.tableViewByteColumnCellFactory = new TableViewByteColumnCellFactory();
        tableViewRowIndexColumnCellFactory = new TableViewRowIndexColumnCellFactory();
    }


    /**
     * 初始化异步任务事件处理器
     */
    private void initializeWorkerStateEventHandler() {
        //redis key task scheduled event handler
        keyTaskWorkerStateEventHandler = event -> {
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
        };

        //redis getValue task success event handler
        valueTaskWorkerStateEventHandler = event -> {
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
        };

        serverInfoWorkerStateEventHandler = event -> {
            if (event.getEventType() == WorkerStateEvent.WORKER_STATE_SCHEDULED) {
                RedisServerInfoDialog dialog = RedisServerInfoDialog.newInstance();
                dialog.getTextArea().textProperty().bind(event.getSource().valueProperty());
                dialog.show();
            }
        };
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
     * 初始化左侧树形结构
     */
    private void initializeTreeView() {
        RedisRootItem rootItem = new RedisRootItem("服务器列表", redisRootItemActionEventHandler);
        rootItem.setExpanded(true);

        serverTreeView.setCellFactory(treeCellFactory);
        serverTreeView.setRoot(rootItem);

        //切换数据库
        serverTreeView.getSelectionModel().selectedItemProperty().addListener(new DatabaseChangeListener());
    }

    /**
     * 初始化 redis key 表格
     */
    private void initializeKeyTableView() {
        keyNoColumn.setCellFactory(tableViewRowIndexColumnCellFactory);
        keyColumn.setCellFactory(tableViewByteColumnCellFactory);
        keyColumn.setCellValueFactory(param -> param.getValue().keyProperty());
        typeColumn.setCellValueFactory(param -> param.getValue().typeProperty());
        sizeColumn.setCellValueFactory(param -> param.getValue().sizeProperty());
        ttlColumn.setCellValueFactory(param -> param.getValue().ttlProperty());
        keyProgressIndicator = new ProgressIndicatorPlaceholder();
        keyTableView.setPlaceholder(keyProgressIndicator);
        keyTableView.setRowFactory(redisKeyTableRowFactory);
        keyTableView.getSelectionModel().selectedItemProperty().addListener(new RedisKeyChangeListener());

    }

    /**
     * 初始化 redis getValue 列表
     */
    private void initializeValueListView() {
        valueTextArea.setWrapText(true);
        valueNoColumn.setCellFactory(tableViewRowIndexColumnCellFactory);
        valueScoreColumn.setCellValueFactory(param -> param.getValue().scoreProperty());
        valueKeyColumn.setCellFactory(tableViewByteColumnCellFactory);
        valueKeyColumn.setCellValueFactory(param -> param.getValue().keyProperty());
        valueColumn.setCellFactory(tableViewByteColumnCellFactory);
        valueColumn.setCellValueFactory(param -> param.getValue().valueProperty());
        valueProgressIndicator = new ProgressIndicatorPlaceholder();
        valueTableView.setPlaceholder(valueProgressIndicator);
        valueTableView.getSelectionModel().selectedItemProperty().addListener(new RedisValueChangeListener());

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
        MessageUtils.init(messageLabel);
    }

    private void updateKeyValueText(byte[] key, byte[] value) {
        if (key != null) {
            keyTextFiled.setText(StringUtils.byteArrayToString(key));
        } else {
            keyTextFiled.setText(null);
        }
        if (value != null) {
            valueTextArea.setText(StringUtils.byteArrayToString(value));
        } else {
            valueTextArea.setText(null);
        }
    }


    private void addNewServer() {
        connectionDialog().showAndWait().ifPresent(property -> {
            RedisServerItem serverItem = new RedisServerItem(property, redisServerItemActionEventHandler);
            serverItem.setExpanded(false);
            serverTreeView.getRoot().getChildren().add(serverItem);
        });
    }

    private CreateRedisServerDialog connectionDialog() {
        RedisServerProperty property = new RedisServerProperty();
        return CreateRedisServerDialog.newInstance(property);
    }

    private class DatabaseChangeListener implements ChangeListener {

        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            if (newValue != null) {
                if (newValue instanceof RedisServerItem) {
                    RedisServerItem item = (RedisServerItem) newValue;
                    server.set(item.getValue().toString());
                    database.set(null);
                } else if (newValue instanceof RedisDatabaseItem) {
                    RedisDatabaseItem value = (RedisDatabaseItem) newValue;
                    RedisServerProperty serverProperty = value.getServerProperty();
                    int db = value.getDatabase();
                    server.set(serverProperty.toString());
                    database.set(db);
                    RedisKeysTask task = new RedisKeysTask(serverProperty, db, typeColumn.isVisible(), ttlColumn.isVisible());
                    TaskManger.getInstance().execute(task, keyTaskWorkerStateEventHandler);
                } else {
                    server.set(null);
                    database.set(null);
                }
            } else {
                server.set(null);
                database.set(null);
            }
        }
    }

    private class RedisKeyChangeListener implements ChangeListener<RedisKey> {

        @Override
        public void changed(ObservableValue<? extends RedisKey> observable, RedisKey oldValue, RedisKey newValue) {
            if (newValue != null) {
                RedisValueTask task = new RedisValueTask(newValue.getServer(), newValue.getDatabase(), newValue.getKey());
                TaskManger.getInstance().execute(task, valueTaskWorkerStateEventHandler);
            }
        }
    }

    private class RedisValueChangeListener implements ChangeListener<RedisValue> {

        @Override
        public void changed(ObservableValue<? extends RedisValue> observable, RedisValue oldValue, RedisValue newValue) {
            if (newValue != null) {
                updateKeyValueText(newValue.getKey(), newValue.getValue());
            } else {
                updateKeyValueText(null, null);
            }
        }
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
            if (!treeItem.isOpen()) {
                connectServer(treeItem, serverProperty);
            }
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
            TaskManger.getInstance().execute(task, serverInfoWorkerStateEventHandler);
        }

        private void connectServer(RedisServerItem treeItem, RedisServerProperty serverProperty) {
            EventHandler<WorkerStateEvent> eventHandler = event -> {
                if (event.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
                    RedisPoolWrapper poolWrapper = (RedisPoolWrapper) event.getSource().getValue();
                    CommonConstant.putConnectionPool(serverProperty.getUuid(), poolWrapper.getPool());
                    for (int i = 0; i < poolWrapper.getDbNum(); i++) {
                        RedisDatabaseItem item = new RedisDatabaseItem(serverProperty, i, redisDatabaseItemActionEventHandler);
                        treeItem.getChildren().add(item);
                    }
                    treeItem.setOpen(true);
                    treeItem.setExpanded(true);
                }
            };
            RedisConnectionPoolTask task = new RedisConnectionPoolTask(serverProperty);
            TaskManger.getInstance().execute(task, eventHandler);
        }

        private void disconnectServer(RedisServerItem serverItem, RedisServerProperty serverProperty) {
            if (serverItem.isOpen()) {
                ObjectPool<StatefulRedisConnection<byte[], byte[]>> pool = CommonConstant.removeConnectionPool(serverProperty.getUuid());
                if (pool != null) {
                    pool.close();
                    serverItem.getChildren().clear();
                    serverItem.setExpanded(false);
                    serverItem.setOpen(false);
                    keyTableView.getItems().clear();
                    valueTableView.getItems().clear();
                    updateKeyValueText(null, null);
                }
            }
        }
    }

    private class RedisDatabaseItemActionEventHandlerImpl implements RedisDatabaseItemActionEventHandler {

        @Override
        public void click(MouseEvent mouseEvent, TreeItem<?> treeItem, RedisServerProperty serverProperty, int database) {
            if (MouseEventUtils.isDoubleClick(mouseEvent)) {
                refreshKey(serverProperty, database);
            }
        }

        @Override
        public void refresh(ActionEvent actionEvent, TreeItem<?> treeItem, RedisServerProperty serverProperty, int database) {
            refreshKey(serverProperty, database);
        }

        @Override
        public void flush(ActionEvent actionEvent, TreeItem<?> treeItem, RedisServerProperty serverProperty, int database) {
            refreshKey(serverProperty, database);
        }

        private void refreshKey(RedisServerProperty serverProperty, int database) {
            RedisKeysTask task = new RedisKeysTask(serverProperty, database, typeColumn.isVisible(), ttlColumn.isVisible());
            TaskManger.getInstance().execute(task, keyTaskWorkerStateEventHandler);
        }
    }

    private class RedisKeyActionEventHandlerImpl implements RedisKeyActionEventHandler {

        @Override
        public void click(MouseEvent mouseEvent, RedisKey redisKey) {
            if (MouseEventUtils.isDoubleClick(mouseEvent) && redisKey != null) {
                RedisValueTask task = new RedisValueTask(redisKey.getServer(), redisKey.getDatabase(), redisKey.getKey());
                TaskManger.getInstance().execute(task, valueTaskWorkerStateEventHandler);
            }
        }

        @Override
        public void copy(ActionEvent actionEvent, RedisKey redisKey) {
            Map<DataFormat, Object> content = new HashMap<>();
            content.put(DataFormat.PLAIN_TEXT, StringUtils.byteArrayToString(redisKey.getKey()));
            Clipboard.getSystemClipboard().setContent(content);
        }

        @Override
        public void delete(ActionEvent actionEvent, RedisKey redisKey) {
            keyTableView.getItems().remove(redisKey);
        }
    }
}
