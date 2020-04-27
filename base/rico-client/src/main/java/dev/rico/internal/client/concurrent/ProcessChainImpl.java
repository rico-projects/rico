/*
 * Copyright 2018-2019 Karakun AG.
 * Copyright 2015-2018 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.rico.internal.client.concurrent;

import dev.rico.client.ClientConfiguration;
import dev.rico.client.concurrent.BackgroundExecutor;
import dev.rico.client.concurrent.ProcessChain;
import dev.rico.client.concurrent.UiExecutor;
import dev.rico.internal.core.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ProcessChainImpl<T> implements ProcessChain<T> {

    private final List<ProcessDescription<?, ?>> processes;

    private final BackgroundExecutor backgroundExecutor;

    private final UiExecutor uiExecutor;

    private final Consumer<Throwable> exceptionConsumer;

    private final Runnable finalRunnable;

    private ProcessChainImpl(final BackgroundExecutor backgroundExecutor, final UiExecutor uiExecutor, final List<ProcessDescription<?, ?>> processes, final Consumer<Throwable> exceptionConsumer, final Runnable finalRunnable) {
        this.backgroundExecutor = Assert.requireNonNull(backgroundExecutor, "backgroundExecutor");
        this.uiExecutor = Assert.requireNonNull(uiExecutor, "uiExecutor");
        Assert.requireNonNull(processes, "processes");
        this.processes = new ArrayList<>(processes);
        this.exceptionConsumer = exceptionConsumer;
        this.finalRunnable = finalRunnable;
    }

    public ProcessChainImpl(final UiExecutor uiExecutor, final BackgroundExecutor backgroundExecutor, final ClientConfiguration clientConfiguration) {
        Assert.requireNonNull(clientConfiguration, "clientConfiguration");
        this.backgroundExecutor = Assert.requireNonNull(backgroundExecutor, "backgroundExecutor");
        this.uiExecutor = Assert.requireNonNull(uiExecutor, "uiExecutor");
        this.processes = new ArrayList<>();
        this.exceptionConsumer = null;
        this.finalRunnable = null;
    }

    @Override
    public <V> ProcessChain<V> addUiFunction(final Function<T, V> function) {
        return addProcessDescription(new ProcessDescription<T, V>(function, ThreadType.PLATFORM));
    }

    @Override
    public <V> ProcessChain<V> addBackgroundFunction(final Function<T, V> function) {
        return addProcessDescription(new ProcessDescription<T, V>(function, ThreadType.EXECUTOR));
    }

    @Override
    public ProcessChain<Void> addUiRunnable(final Runnable runnable) {
        return addRunnable(runnable, ThreadType.PLATFORM);
    }

    @Override
    public ProcessChain<Void> addBackgroundRunnable(final Runnable runnable) {
        return addRunnable(runnable, ThreadType.EXECUTOR);
    }

    @Override
    public ProcessChain<Void> addUiConsumer(final Consumer<T> consumer) {
        return addConsumer(consumer, ThreadType.PLATFORM);
    }

    @Override
    public ProcessChain<Void> addBackgroundConsumer(final Consumer<T> consumer) {
        return addConsumer(consumer, ThreadType.EXECUTOR);
    }

    @Override
    public <V> ProcessChain<V> addUiSupplier(final Supplier<V> supplier) {
        return addSupplier(supplier, ThreadType.PLATFORM);
    }

    @Override
    public <V> ProcessChain<V> addBackgroundSupplier(final Supplier<V> supplier) {
        return addSupplier(supplier, ThreadType.EXECUTOR);
    }

    private ProcessChain<Void> addRunnable(final Runnable runnable, final ThreadType type) {
        Assert.requireNonNull(runnable, "runnable");
        return addProcessDescription(new ProcessDescription<T, Void>(e -> {
            runnable.run();
            return null;
        }, type));
    }

    private ProcessChain<Void> addConsumer(final Consumer<T> consumer, final ThreadType type) {
        Assert.requireNonNull(consumer, "consumer");
        return addProcessDescription(new ProcessDescription<T, Void>(e -> {
            consumer.accept(e);
            return null;
        }, type));
    }

    private <V> ProcessChain<V> addSupplier(final Supplier<V> supplier, final ThreadType type) {
        Assert.requireNonNull(supplier, "supplier");
        return addProcessDescription(new ProcessDescription<T, V>(e -> supplier.get(), type));
    }

    private <V> ProcessChain<V> addProcessDescription(final ProcessDescription<T, V> processDescription) {
        Assert.requireNonNull(processDescription, "processDescription");
        processes.add(processDescription);
        return new ProcessChainImpl<V>(backgroundExecutor, uiExecutor, processes, exceptionConsumer, finalRunnable);
    }


    @Override
    public ProcessChain<T> onException(final Consumer<Throwable> exceptionConsumer) {
        return new ProcessChainImpl<T>(backgroundExecutor, uiExecutor, processes, exceptionConsumer, finalRunnable);
    }

    @Override
    public ProcessChain<T> withUiFinal(final Runnable finalRunnable) {
        return new ProcessChainImpl<T>(backgroundExecutor, uiExecutor, processes, exceptionConsumer, finalRunnable);
    }

    private <U, V> V execute(final U inputParameter, final ProcessDescription<U, V> processDescription) throws InterruptedException, ExecutionException {
        Assert.requireNonNull(processDescription, "processDescription");
        if (Objects.equals(processDescription.getThreadType(), ThreadType.EXECUTOR)) {
            return processDescription.getFunction().apply(inputParameter);
        } else {
            final CompletableFuture<V> futureResult = new CompletableFuture<>();
            uiExecutor.execute(() -> {
                final V result = processDescription.getFunction().apply(inputParameter);
                futureResult.complete(result);
            });
            return futureResult.get();
        }
    }

    @Override
    public Callable<T> run() {
        final Callable<T> task = new Callable<T>() {

            @Override
            public T call() throws Exception {
                try {
                    Object lastResult = null;
                    for (final ProcessDescription<?, ?> processDescription : processes) {
                        lastResult = execute(lastResult, (ProcessDescription<Object, ?>) processDescription);
                    }
                    return (T) lastResult;
                } catch (final Exception e) {
                    if (exceptionConsumer != null) {
                        final CompletableFuture<Void> futureResult = new CompletableFuture<>();
                        uiExecutor.execute(() -> {
                            exceptionConsumer.accept(e);
                            futureResult.complete(null);
                        });
                        futureResult.get();
                    }
                    throw e;
                } finally {
                    if (finalRunnable != null) {
                        final CompletableFuture<Void> futureResult = new CompletableFuture<>();
                        uiExecutor.execute(() -> {
                            finalRunnable.run();
                            futureResult.complete(null);
                        });
                        futureResult.get();
                    }
                }
            }

        };
        backgroundExecutor.submit(task);
        return task;
    }
}
