package dev.rico.client.concurrent;

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

}
