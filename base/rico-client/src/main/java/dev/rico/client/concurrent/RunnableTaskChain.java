package dev.rico.client.concurrent;

import java.util.concurrent.CompletableFuture;

/**
 * A task chain which is ready to be run.
 *
 * @param <T> the type of the retuned future.
 */
public interface RunnableTaskChain<T> {
    /**
     * Completes the chain and triggers its execution.
     *
     * @return a future which will be completed once all tasks of the chain have completed
     */
    CompletableFuture<T> run();
}
