package dev.rico.client.concurrent;

import dev.rico.core.functional.CheckedConsumer;
import dev.rico.core.functional.CheckedFunction;

import java.util.function.Function;

/**
 * A batch of tasks.
 * The tasks are executed in the order they are registered to the batch.
 * <p>
 * Execution can be switched between ui and background threads using the methods
 * {@link #ui()} and {@link #background()}.
 * <p>
 * Exception handlers can be registered to handle exceptions gracefully.
 *
 * @param <T> the type of the input value to this next task of this batch.
 */
public interface TaskChainWithInput<T> extends RunnableTaskChain<T> {
    /**
     * Register a mapper task.
     * The value returned by the mapper task will be used as the input to the next task.
     *
     * @param function the mapper to call when executing the batch
     * @param <U>      the type of the value returned by the mapper
     * @return a task batch with input for method chaining
     */
    <U> TaskChainWithInput<U> map(CheckedFunction<T, U> function);

    /**
     * Register a consumer task.
     *
     * @param consumer the consumer to call when executing the batch
     * @return a task batch for method chaining
     */
    TaskChain consume(CheckedConsumer<T> consumer);

    /**
     * Switches the execution to a background thread.
     * All following tasks will be executed in a background thread.
     * Until {@link #ui()} is called.
     *
     * @return the task queue for method chaining
     */
    TaskChainWithInput<T> background();

    /**
     * Switches the execution to a ui thread.
     * All following tasks will be executed in the ui thread.
     * Until {@link #background()} is called.
     *
     * @return the task queue for method chaining
     */
    TaskChainWithInput<T> ui();

    /**
     * Register an exception handler.
     * The exception handler will only be called if one of the previous task throws an exception.
     * If this is not the case the exception handler is skipped.
     *
     * @param exceptionHandler the handler to call if an exception occurs while executing the batch
     * @return the task batch with input for method chaining
     */
    TaskChainWithInput<T> onException(Function<Throwable, T> exceptionHandler);

    /**
     * Register a last task.
     * This task is executed independent if the previous tasks threw an exception or nor.
     * At the same time this method completes the batch.
     *
     * @param runnable the final task to call when the batch is executed
     * @return a future which will be completed once all tasks of the batch have completed
     */
    RunnableTaskChain<T> thenFinally(Runnable runnable);
}
