package com.jean.redis.client.mange;

import com.jean.redis.client.factory.RedisThreadFactory;
import com.jean.redis.client.task.BaseTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskManger {

    private static final Object lock = new Object();

    private static final TaskManger instance = new TaskManger();

    private final ObservableMap<String, List<BaseTask>> runningTasks;

    private final ExecutorService executorService;

    private TaskManger() {
        int thread_num = Runtime.getRuntime().availableProcessors() * 2;
        this.executorService = Executors.newFixedThreadPool(thread_num, new RedisThreadFactory());
        this.runningTasks = FXCollections.observableMap(new ConcurrentHashMap<>());
    }

    public static TaskManger getInstance() {
        return instance;
    }

    public void execute(BaseTask<?> task) {
        this.execute(task, null);
    }

    public void execute(BaseTask<?> task, EventHandler<WorkerStateEvent> eventHandler) {
        if (eventHandler != null) {
            task.addEventHandler(WorkerStateEvent.WORKER_STATE_READY, eventHandler);
            task.addEventHandler(WorkerStateEvent.WORKER_STATE_SCHEDULED, eventHandler);
            task.addEventHandler(WorkerStateEvent.WORKER_STATE_RUNNING, eventHandler);
            task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, eventHandler);
            task.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, eventHandler);
            task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, eventHandler);
        }
        cacheTask(task);
        executorService.execute(task);
    }

    private void cacheTask(BaseTask<?> task) {
        synchronized (lock) {
            if (!runningTasks.containsKey(task.getTaskType())) {
                runningTasks.put(task.getTaskType(), new ArrayList<>());
            } else {
                List<BaseTask> tasks = runningTasks.get(task.getTaskType());
                tasks.forEach(item -> item.cancel(false));
                tasks.clear();
            }
            runningTasks.get(task.getTaskType()).add(task);
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
