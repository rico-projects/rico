package dev.rico.internal.client.concurrent;

import dev.rico.client.concurrent.RunnableTaskChain;
import dev.rico.client.concurrent.TaskChain;
import dev.rico.client.concurrent.TaskChainWithInput;
import dev.rico.core.functional.CheckedRunnable;
import dev.rico.core.functional.CheckedSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static dev.rico.internal.core.Assert.requireNonNull;

public class TaskChainImpl extends RunnableTaskChainImpl<Void> implements TaskChain {
    private final Executor backgroundExecutor;
    private final Executor uiExecutor;

    public TaskChainImpl(Executor backgroundExecutor, Executor uiExecutor) {
        this(new ArrayList<>(), backgroundExecutor, backgroundExecutor, uiExecutor);
    }

    TaskChainImpl(List<ChainStep> steps, Executor executor, Executor backgroundExecutor, Executor uiExecutor) {
        super(steps, executor);
        this.backgroundExecutor = requireNonNull(backgroundExecutor, "backgroundExecutor");
        this.uiExecutor = requireNonNull(uiExecutor, "uiExecutor");
    }

    @Override
    public <T> TaskChainWithInput<T> supply(CheckedSupplier<T> supplier) {
        requireNonNull(supplier, "supplier");
        steps.add(new ChainStep(executor, TaskType.TASK, i -> supplier.get()));
        return new TaskChainWithInputImpl<>(steps, executor, backgroundExecutor, uiExecutor);
    }

    @Override
    public TaskChain execute(CheckedRunnable runnable) {
        requireNonNull(runnable, "runnable");
        steps.add(new ChainStep(executor, TaskType.TASK, i -> {runnable.run(); return null;}));
        return this;
    }

    @Override
    public TaskChain background() {
        switchExecutor(backgroundExecutor);
        return this;
    }

    @Override
    public TaskChain ui() {
        switchExecutor(uiExecutor);
        return this;
    }

    @Override
    public TaskChain onException(Consumer<Throwable> exceptionHandler) {
        requireNonNull(exceptionHandler, "exceptionHandler");
        steps.add(new ChainStep(executor, TaskType.EXCEPTION_HANDLER, i -> {exceptionHandler.accept((Throwable) i); return null;}));
        return this;
    }

    @Override
    public RunnableTaskChain<Void> thenFinally(Runnable runnable) {
        requireNonNull(runnable, "runnable");
        steps.add(new ChainStep(executor, TaskType.FINALLY, i -> {runnable.run(); return null;}));
        return this;
    }
}
