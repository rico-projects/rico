package dev.rico.client.concurrent;

import dev.rico.core.concurrent.Task;
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

    List<Task> getAllTasks();

}
