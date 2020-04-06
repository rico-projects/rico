package dev.rico.client.concurrent;

import dev.rico.internal.client.concurrent.TaskHelper;
import dev.rico.internal.core.Assert;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
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

    default <T> CompletableFuture<T> executeTask(final Callable<T> task) {
        Assert.requireNonNull(task, "task");
        CompletableFuture<T> future = new CompletableFuture<>();
        submit(() -> {
            try {
                future.complete(task.call());
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    default CompletableFuture<Void> executeTask(final Runnable task) {
        Assert.requireNonNull(task, "task");
        CompletableFuture<Void> future = new CompletableFuture<>();
        submit(() -> {
            try {
                task.run();
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
