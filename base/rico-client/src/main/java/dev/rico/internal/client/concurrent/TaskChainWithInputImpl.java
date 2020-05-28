package dev.rico.internal.client.concurrent;

import dev.rico.client.concurrent.RunnableTaskChain;
import dev.rico.client.concurrent.TaskChain;
import dev.rico.client.concurrent.TaskChainWithInput;
import dev.rico.core.functional.CheckedConsumer;
import dev.rico.core.functional.CheckedFunction;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Function;

class TaskChainWithInputImpl<T> extends RunnableTaskChainImpl<T> implements TaskChainWithInput<T> {
    private final Executor backgroundExecutor;
    private final Executor uiExecutor;

    TaskChainWithInputImpl(List<RunnableTaskChainImpl.ChainStep> steps, Executor executor, Executor backgroundExecutor, Executor uiExecutor) {
        super(steps, executor);
        this.backgroundExecutor = backgroundExecutor;
        this.uiExecutor = uiExecutor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> TaskChainWithInput<U> map(CheckedFunction<T, U> function) {
        steps.add(new ChainStep(executor, TaskType.TASK, i -> function.apply((T) i)));
        return (TaskChainWithInput<U>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TaskChain consume(CheckedConsumer<T> consumer) {
        steps.add(new ChainStep(executor, TaskType.TASK, i -> {consumer.accept((T) i); return null;}));
        return new TaskChainImpl(steps, executor, backgroundExecutor, uiExecutor);
    }

    @Override
    public TaskChainWithInput<T> background() {
        switchExecutor(backgroundExecutor);
        return this;
    }

    @Override
    public TaskChainWithInput<T> ui() {
        switchExecutor(uiExecutor);
        return this;
    }

    @Override
    public TaskChainWithInput<T> onException(Function<Throwable, T> exceptionHandler) {
        steps.add(new ChainStep(executor, TaskType.EXCEPTION_HANDLER, i -> exceptionHandler.apply((Throwable) i)));
        return this;
    }

    @Override
    public RunnableTaskChain<T> thenFinally(Runnable runnable) {
        steps.add(new ChainStep(executor, TaskType.FINALLY, i -> {runnable.run(); return null;}));
        return this;
    }
}
