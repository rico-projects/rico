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
 * ...
 */
interface TaskChain {
    <T> TaskChainWithInput<T> supply(CheckedSupplier<T> supplier);

    TaskChain execute(CheckedRunnable runnable);

    TaskChain background();

    TaskChain ui();

    TaskChain onException(Consumer<Throwable> consumer);

    <T> TaskChainWithInput<T> onException(Function<Throwable, T> function);

    CompletableFuture<Void> thenFinally(Runnable runnable);

    CompletableFuture<Void> run();
}

interface TaskChainWithInput<T> {
    <U> TaskChainWithInput<U> map(CheckedFunction<T, U> function);

    TaskChain consume(CheckedConsumer<T> consumer);

    TaskChain background();

    TaskChain ui();

    TaskChainWithInput<T> onException(Function<Throwable, T> function);

    CompletableFuture<T> thenFinally(Runnable runnable);

    CompletableFuture<T> run();
}

class Example {

    void foo(TaskChain taskChain) {
        taskChain.supply(() -> "blah")
                .switchToUi().map(s -> "blubb")
        ;

        taskChain.ui().supply(() -> "blah")
                .consume(System.out::println)
                .onException(e -> {
                    System.out.println(e.toString());
                })
        ;

        taskChain.supply()

        taskChain.supplyFromUi(() -> "blah ui")
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
