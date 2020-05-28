package dev.rico.client.concurrent;

import dev.rico.core.functional.CheckedRunnable;
import dev.rico.core.functional.CheckedSupplier;

import java.util.function.Consumer;

/**
 * A batch of tasks.
 * The tasks are executed in the order they are registered to the batch.
 *
 * Execution can be switched between ui and background threads using the methods
 * {@link #ui()} and {@link #background()}.
 *
 * Exception handlers can be registered to handle exceptions gracefully.
 */
public interface TaskChain extends RunnableTaskChain<Void> {
    /**
     * Register a supplier task.
     * The value returned by the supplier will be used as the input to the next task.
     *
     * @param supplier the supplier to call when executing the batch
     * @param <T>      the type of the value returned by the supplier
     * @return a task batch with input for method chaining
     */
    <T> TaskChainWithInput<T> supply(CheckedSupplier<T> supplier);

    /**
     * Register a runnable task.
     *
     * @param runnable the task to run when executing the batch
     * @return the task batch for method chaining
     */
    TaskChain execute(CheckedRunnable runnable);

    /**
     * Switches the execution to a background thread.
     * All following tasks will be executed in a background thread.
     * Until {@link #ui()} is called.
     *
     * @return the task batch for method chaining
     */
    TaskChain background();

    /**
     * Switches the execution to a ui thread.
     * All following tasks will be executed in the ui thread.
     * Until {@link #background()} is called.
     *
     * @return the task batch for method chaining
     */
    TaskChain ui();

    /**
     * Register an exception handler.
     * The exception handler will only be called if one of the previous task throws an exception.
     * If this is not the case the exception handler is skipped.
     *
     * @param exceptionHandler the handler to call if an exception occurs while executing the batch
     * @return the task batch for method chaining
     */
    TaskChain onException(Consumer<Throwable> exceptionHandler);

    /**
     * Register a last task.
     * This task is executed independent if the previous tasks threw an exception or nor.
     * At the same time this method completes the batch.
     *
     * @param runnable the final task to call when the batch is executed
     * @return a future which will be completed once all tasks of the batch have completed
     */
    RunnableTaskChain<Void> thenFinally(Runnable runnable);
}
