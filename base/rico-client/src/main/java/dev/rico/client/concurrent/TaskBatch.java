package dev.rico.client.concurrent;

import dev.rico.core.functional.CheckedConsumer;
import dev.rico.core.functional.CheckedFunction;
import dev.rico.core.functional.CheckedRunnable;
import dev.rico.core.functional.CheckedSupplier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A batch of tasks.
 * The tasks are executed in the order they are registered to the batch.
 *
 * Execution can be switched between ui and background threads using the methods
 * {@link #ui()} and {@link #background()}.
 *
 * Exception handlers can be registered to handle exceptions gracefully.
 */
interface TaskBatch extends CompletedTaskBatch<Void> {
    /**
     * Register a supplier task.
     * The value returned by the supplier will be used as the input to the next task.
     *
     * @param supplier the supplier to call when executing the batch
     * @param <T> the type of the value returned by the supplier
     * @return a task batch with input for method chaining
     */
    <T> TaskBatchWithInput<T> supply(CheckedSupplier<T> supplier);

    /**
     * Register a runnable task.
     *
     * @param runnable the task to run when executing the batch
     * @return the task batch for method chaining
     */
    TaskBatch execute(CheckedRunnable runnable);

    /**
     * Switches the execution to a background thread.
     * All following tasks will be executed in a background thread.
     * Until {@link #ui()} is called.
     *
     * @return the task batch for method chaining
     */
    TaskBatch background();

    /**
     * Switches the execution to a ui thread.
     * All following tasks will be executed in the ui thread.
     * Until {@link #background()} is called.
     *
     * @return the task batch for method chaining
     */
    TaskBatch ui();

    /**
     * Register an exception handler.
     * The exception handler will only be called if one of the previous task throws an exception.
     * If this is not the case the exception handler is skipped.
     *
     * @param exceptionHandler the handler to call if an exception occurs while executing the batch
     * @return the task batch for method chaining
     */
    TaskBatch onException(Consumer<Throwable> exceptionHandler);

    /**
     * Register a last task.
     * This task is executed independent if the previous tasks threw an exception or nor.
     * At the same time this method completes the batch.
     *
     * @param runnable the final task to call when the batch is executed
     * @return a future which will be completed once all tasks of the batch have completed
     */
    CompletedTaskBatch<Void> thenFinally(Runnable runnable);
}

/**
 * A batch of tasks.
 * The tasks are executed in the order they are registered to the batch.
 *
 * Execution can be switched between ui and background threads using the methods
 * {@link #ui()} and {@link #background()}.
 *
 * Exception handlers can be registered to handle exceptions gracefully.
 *
 * @param <T> the type of the input value to this next task of this batch.
 */
interface TaskBatchWithInput<T> extends CompletedTaskBatch<T> {
    /**
     * Register a mapper task.
     * The value returned by the mapper task will be used as the input to the next task.
     *
     * @param function the mapper to call when executing the batch
     * @param <U> the type of the value returned by the mapper
     * @return a task batch with input for method chaining
     */
    <U> TaskBatchWithInput<U> map(CheckedFunction<T, U> function);

    /**
     * Register a consumer task.
     *
     * @param consumer the consumer to call when executing the batch
     * @return a task batch for method chaining
     */
    TaskBatch consume(CheckedConsumer<T> consumer);

    /**
     * Switches the execution to a background thread.
     * All following tasks will be executed in a background thread.
     * Until {@link #ui()} is called.
     *
     * @return the task queue for method chaining
     */
    TaskBatchWithInput<T> background();

    /**
     * Switches the execution to a ui thread.
     * All following tasks will be executed in the ui thread.
     * Until {@link #background()} is called.
     *
     * @return the task queue for method chaining
     */
    TaskBatchWithInput<T> ui();

    /**
     * Register an exception handler.
     * The exception handler will only be called if one of the previous task throws an exception.
     * If this is not the case the exception handler is skipped.
     *
     * @param exceptionHandler the handler to call if an exception occurs while executing the batch
     * @return the task batch with input for method chaining
     */
    TaskBatchWithInput<T> onException(Function<Throwable, T> exceptionHandler);

    /**
     * Register a last task.
     * This task is executed independent if the previous tasks threw an exception or nor.
     * At the same time this method completes the batch.
     *
     * @param runnable the final task to call when the batch is executed
     * @return a future which will be completed once all tasks of the batch have completed
     */
    CompletedTaskBatch<T> thenFinally(Runnable runnable);
}

interface CompletedTaskBatch<T> {
    /**
     * Completes the batch and triggers its execution.
     *
     * @return a future which will be completed once all tasks of the batch have completed
     */
    CompletableFuture<T> run();
}

class Example {

    void foo(TaskBatch taskBatch) {
        taskBatch.supply(() -> "blah")
                .switchToUi().map(s -> "blubb")
        ;

        taskBatch.ui().supply(() -> "blah")
                .consume(System.out::println)
                .onException(e -> {
                    System.out.println(e.toString());
                })
        ;

        taskBatch.supply()

        taskBatch.supplyFromUi(() -> "blah ui")
        ;

        try {
            // supply
            // consume
        } catch (Exception e) {
            // onException
        }

        Object x;
        try {
            // x =supply
        } catch (Exception e) {
            // x = onException
        }
        // consume(x)
    }

    List<String> readFromFile(File f) throws IOException {
        try (final FileInputStream in = new FileInputStream(f)) {

        }
    }
}
