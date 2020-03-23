package com.jean.redis.client.view.handler.impl;

import com.jean.redis.client.mange.TaskManger;
import com.jean.redis.client.model.RedisKey;
import com.jean.redis.client.model.RedisServerProperty;
import com.jean.redis.client.task.RedisKeysTask;
import com.jean.redis.client.util.ViewUtils;
import com.jean.redis.client.view.ProgressIndicatorPlaceholder;
import com.jean.redis.client.view.RedisDatabaseItem;
import com.jean.redis.client.view.handler.IRedisDatabaseItemActionEventHandler;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * @author jinshubao
 */
public class RedisDatabaseItemActionEventHandlerImpl implements IRedisDatabaseItemActionEventHandler {

    private final TableView<RedisKey> keyTableView;

    private final TableColumn<RedisKey, Integer> keyNoColumn;
    private final TableColumn<RedisKey, byte[]> keyColumn;
    private final TableColumn<RedisKey, String> typeColumn;
    private final TableColumn<RedisKey, Number> sizeColumn;
    private final TableColumn<RedisKey, Number> ttlColumn;
    private final Label serverInfoLabel;
    private final EventHandler<WorkerStateEvent> eventEventHandler;

    @SuppressWarnings("unchecked")
    public RedisDatabaseItemActionEventHandlerImpl() {

        this.keyTableView = ViewUtils.getInstance().getKeyTableView();
        this.keyNoColumn = ViewUtils.getInstance().getKeyNoColumn();
        this.keyColumn = ViewUtils.getInstance().getKeyColumn();
        this.typeColumn = ViewUtils.getInstance().getTypeColumn();
        this.sizeColumn = ViewUtils.getInstance().getSizeColumn();
        this.ttlColumn = ViewUtils.getInstance().getTtlColumn();
        this.serverInfoLabel = ViewUtils.getInstance().getServerInfoLabel();

        this.eventEventHandler = event -> {
            ProgressIndicatorPlaceholder keyProgressIndicator = (ProgressIndicatorPlaceholder) keyTableView.getPlaceholder();
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
    }

    @Override
    public void onDoubleClick(RedisDatabaseItem treeItem) {
        this.refresh(treeItem);
    }

    @Override
    public void refresh(RedisDatabaseItem treeItem) {
        this.refreshKey(treeItem.getServerProperty(), treeItem.getDatabase());
    }

    @Override
    public void flush(RedisDatabaseItem treeItem) {

    }

    @Override
    public void onSelected(RedisDatabaseItem treeItem) {
        RedisServerProperty serverProperty = treeItem.getServerProperty();
        this.serverInfoLabel.setText(serverProperty + ":" + treeItem.getDatabase());
        this.refreshKey(serverProperty, treeItem.getDatabase());
    }

    private void refreshKey(RedisServerProperty serverProperty, int database) {
        RedisKeysTask task = new RedisKeysTask(serverProperty, database, typeColumn.isVisible(), ttlColumn.isVisible());
        TaskManger.getInstance().execute(task, new WeakEventHandler<>(eventEventHandler));
    }
}
