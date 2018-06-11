package dev.rico.internal.client.concurrent;

import dev.rico.client.concurrent.BackgroundExecutor;
import dev.rico.core.concurrent.Task;
import dev.rico.internal.core.Assert;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class BackgroundExecutorImpl implements BackgroundExecutor {

    private final ExecutorService executorService;

    private final List<Task> tasks;

    public BackgroundExecutorImpl(final ExecutorService executorService) {
        this.executorService = Assert.requireNonNull(executorService, "executorService");
        tasks = new CopyOnWriteArrayList<>();
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
            final Future<T> future = executorService.submit(task);
            tasks.add(new Task() {
                @Override
                public void cancel() {
                    future.cancel(true);
                }
            });
            return future;
    }

    @Override
    public void execute(final Runnable command) {
        submit(command);
    }

    @Override
    public List<Task> getAllTasks() {
        return Collections.unmodifiableList(tasks);
    }
}
