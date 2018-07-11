package dev.rico.internal.client.concurrent;

import dev.rico.client.concurrent.BackgroundExecutor;
import dev.rico.client.concurrent.BackgroundTask;
import dev.rico.internal.core.Assert;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class BackgroundExecutorImpl implements BackgroundExecutor {

    private final ExecutorService executorService;

    private final List<BackgroundTask> tasks;

    public BackgroundExecutorImpl(final ExecutorService executorService) {
        this.executorService = Assert.requireNonNull(executorService, "executorService");
        tasks = new CopyOnWriteArrayList<>();
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        Assert.requireNonNull(task, "task");
        final AtomicBoolean started = new AtomicBoolean(false);
        final Future<T> future = executorService.submit(() -> {
            TaskHelper.getInstance().resetCurrent();
            started.set(true);
            return task.call();
        });
        tasks.add(new BackgroundTask() {

            @Override
            public String getName() {
                return TaskHelper.getInstance().getCurrentName();
            }

            @Override
            public String geDescription() {
                return TaskHelper.getInstance().getCurrentDescription();
            }

            @Override
            public void cancel() {
                future.cancel(true);
            }

            @Override
            public boolean isCancelled() {
                return future.isCancelled();
            }

            @Override
            public boolean isDone() {
                return future.isDone();
            }

            @Override
            public boolean isRunning() {
                return (!isDone() && !isCancelled() && started.get());
            }
        });
        return future;
    }

    @Override
    public void execute(final Runnable command) {
        submit(command);
    }

    @Override
    public List<BackgroundTask> getAllTasks() {
        return Collections.unmodifiableList(tasks);
    }
}
