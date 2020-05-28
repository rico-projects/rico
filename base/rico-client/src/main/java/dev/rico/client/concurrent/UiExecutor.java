package dev.rico.client.concurrent;

import dev.rico.client.Client;
import dev.rico.internal.client.concurrent.TaskChainImpl;
import dev.rico.internal.core.Assert;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface UiExecutor extends Executor {

    default CompletableFuture<Void> executeTask(final Runnable task) {
        Assert.requireNonNull(task, "task");
        final CompletableFuture<Void> future = new CompletableFuture<>();
        execute(() -> {
            try {
                task.run();
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    default TaskChain createUiTaskChain() {
        return createBackgroundTaskChain().ui();
    }

    default TaskChain createUiTaskChain(Executor backgroundExecutor) {
        return createBackgroundTaskChain(backgroundExecutor).ui();
    }

    default TaskChain createBackgroundTaskChain() {
        return createBackgroundTaskChain(Client.getService(BackgroundExecutor.class));
    }

    default TaskChain createBackgroundTaskChain(Executor backgroundExecutor) {
        return new TaskChainImpl(backgroundExecutor, this);
    }
}
