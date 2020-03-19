package com.jean.redis.client.view.handler.impl;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.dialog.RedisServerInfoDialog;
import com.jean.redis.client.view.handler.BaseMouseEventHandler;
import com.jean.redis.client.view.handler.IRedisDatabaseItemActionEventHandler;
import com.jean.redis.client.view.handler.IRedisServerItemActionEventHandler;
import com.jean.redis.client.view.RedisDatabaseItem;
import com.jean.redis.client.view.RedisServerItem;
import com.jean.redis.client.mange.TaskManger;
import com.jean.redis.client.model.RedisKey;
import com.jean.redis.client.model.RedisPoolWrapper;
import com.jean.redis.client.model.RedisServerProperty;
import com.jean.redis.client.model.RedisValue;
import com.jean.redis.client.task.BaseTask;
import com.jean.redis.client.task.RedisConnectionPoolTask;
import io.lettuce.core.api.StatefulRedisConnection;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.apache.commons.pool2.ObjectPool;

public class RedisServerItemActionEventHandler extends BaseMouseEventHandler<RedisServerItem> implements IRedisServerItemActionEventHandler {

    private final TableView<RedisKey> keyTableView;
    private final TableView<RedisValue> valueTableView;
    private final TextField keyTextFiled;
    private final TextArea valueTextArea;
    private final EventHandler<WorkerStateEvent> serverInfoWorkerStateEventHandler;
    private final WeakEventHandler<WorkerStateEvent> weakServerInfoWorkerStateEventHandler;

    private IRedisDatabaseItemActionEventHandler redisDatabaseItemActionEventHandler;

    public RedisServerItemActionEventHandler(Node root) {
        super(root);
        SplitPane splitPane = (SplitPane) root.lookup("#splitPane");
        this.keyTableView = (TableView<RedisKey>) splitPane.getItems().stream().filter(item -> "keyTableView".equals(item.getId())).findFirst().orElseThrow(()-> new RuntimeException("id='serverTreeView' not fund"));
        SplitPane  valueSplitPane = (SplitPane) splitPane.getItems().stream().filter(item -> "valueSplitPane".equals(item.getId())).findFirst().orElseThrow(()-> new RuntimeException("id='valueSplitPane' not fund"));
        this.valueTableView = (TableView<RedisValue>) valueSplitPane.getItems().stream().filter(item -> "valueTableView".equals(item.getId())).findFirst().orElseThrow(()-> new RuntimeException("id='valueTableView' not fund"));
        GridPane valueGridPane = (GridPane) valueSplitPane.getItems().stream().filter(item -> "valueGridPane".equals(item.getId())).findFirst().orElseThrow(()-> new RuntimeException("id='valueGridPane' not fund"));
        this.keyTextFiled = (TextField) valueGridPane.lookup("#keyTextFiled");
        this.valueTextArea = (TextArea) valueGridPane.lookup("#valueTextArea");

        this.serverInfoWorkerStateEventHandler = event -> {
            if (event.getEventType() == WorkerStateEvent.WORKER_STATE_SCHEDULED) {
                RedisServerInfoDialog dialog = RedisServerInfoDialog.newInstance();
                dialog.getTextArea().setText((String) event.getSource().getValue());
                dialog.show();
            }
        };

        this.weakServerInfoWorkerStateEventHandler = new WeakEventHandler<>(this.serverInfoWorkerStateEventHandler);
        this.redisDatabaseItemActionEventHandler = new RedisDatabaseItemActionEventHandlerImpl(root);
    }

    @Override
    public void onDoubleClick(RedisServerItem redisServerItem) {
        this.open(redisServerItem);
    }

    @Override
    public void open(RedisServerItem treeItem) {
        if (!treeItem.isOpen()) {
            connectServer(treeItem);
        }
    }

    @Override
    public void close(RedisServerItem treeItem) {
        disconnectServer(treeItem);
    }

    @Override
    public void delete(RedisServerItem treeItem) {
        disconnectServer(treeItem);
        treeItem.getParent().getChildren().remove(treeItem);
    }

    @Override
    public void property(RedisServerItem serverItem) {
        RedisServerProperty serverProperty = serverItem.getServerProperty();
        TaskManger.getInstance().execute(new RedisServerInfoTask(serverProperty), this.weakServerInfoWorkerStateEventHandler);
    }


    private void connectServer(RedisServerItem serverItem) {
        RedisServerProperty serverProperty = serverItem.getServerProperty();
        EventHandler<WorkerStateEvent> eventHandler = event -> {
            if (event.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
                RedisPoolWrapper poolWrapper = (RedisPoolWrapper) event.getSource().getValue();
                CommonConstant.putConnectionPool(serverProperty.getUuid(), poolWrapper.getPool());
                for (int i = 0; i < poolWrapper.getDbNum(); i++) {
                    RedisDatabaseItem item = new RedisDatabaseItem(serverProperty, i, redisDatabaseItemActionEventHandler);
                    serverItem.getChildren().add(item);
                }
                serverItem.setOpen(true);
                serverItem.setExpanded(true);
            }
        };
        RedisConnectionPoolTask task = new RedisConnectionPoolTask(serverProperty);
        TaskManger.getInstance().execute(task, eventHandler);
    }

    private void disconnectServer(RedisServerItem serverItem) {
        RedisServerProperty serverProperty = serverItem.getServerProperty();
        if (serverItem.isOpen()) {
            ObjectPool<StatefulRedisConnection<byte[], byte[]>> pool = CommonConstant.removeConnectionPool(serverProperty.getUuid());
            if (pool != null) {
                pool.close();
                serverItem.getChildren().clear();
                serverItem.setExpanded(false);
                serverItem.setOpen(false);
                this.keyTableView.getItems().clear();
                this.valueTableView.getItems().clear();

                keyTextFiled.setText(null);
                valueTextArea.setText(null);
            }
        }
    }


    private static class RedisServerInfoTask extends BaseTask<String> {


        public RedisServerInfoTask(RedisServerProperty serverProperty) {
            super(serverProperty);
            updateMessage("服务器信息");
        }

        @Override
        protected String call() throws Exception {
            try (StatefulRedisConnection<byte[], byte[]> connection = getConnection()) {
                return connection.sync().info();
            }
        }

        @Override
        public String toString() {
            return "server-info-task-" + super.toString();
        }
    }
}