package dev.rico.client.concurrent;

import dev.rico.internal.client.concurrent.TaskHelper;
import dev.rico.internal.core.Assert;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public interface BackgroundExecutor extends Executor {

    <T> Future<T> submit(Callable<T> task);

    default <T> Future<T> submit(final Runnable task, final T result) {
        Assert.requireNonNull(task, "task");
        return submit(() -> {
            task.run();
            return result;
        });
    }

    default Future<?> submit(final Runnable task) {
        return submit(task, null);
    }

    List<BackgroundTask> getAllTasks();

    static void setCurrentTaskName(final String name) {
        TaskHelper.getInstance().setTaskName(name);
    }

    static void setCurrentDescription(final String description) {
        TaskHelper.getInstance().setTaskDescription(description);
    }

}
