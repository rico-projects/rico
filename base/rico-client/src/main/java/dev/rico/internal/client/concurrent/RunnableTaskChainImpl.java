package dev.rico.internal.client.concurrent;

import dev.rico.client.concurrent.RunnableTaskChain;
import dev.rico.core.functional.CheckedFunction;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

import static dev.rico.internal.client.concurrent.TaskType.EXCEPTION_HANDLER;
import static dev.rico.internal.client.concurrent.TaskType.TASK;
import static dev.rico.internal.core.Assert.requireNonNull;

abstract class RunnableTaskChainImpl<T> implements RunnableTaskChain<T> {

    final List<ChainStep> steps;

    protected Executor executor;

    RunnableTaskChainImpl(List<ChainStep> steps, Executor executor) {
        this.steps = requireNonNull(steps, "steps");
        this.executor = requireNonNull(executor, "executor");
    }

    void switchExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<T> run() {

        CompletableFuture<?> future = CompletableFuture.completedFuture(null);

        for (ChainStep step : steps) {
            future = future.handleAsync((i, e) -> {
                if (e == null) {
                    return handleNormalCase(step, i);
                } else {
                    return handleExceptionCase(step, e);
                }
            }, step.executor);
        }
        return (CompletableFuture<T>) future;
    }

    private Object handleNormalCase(ChainStep step, Object input) {
        try {
            if (step.taskType == EXCEPTION_HANDLER) {
                return input;
            } else {
                return step.task.apply(input);
            }
        } catch (Exception e) {
            throw toRuntimeException(e);
        }
    }

    private Object handleExceptionCase(ChainStep step, Throwable t) {
        try {
            if (step.taskType == TASK) {
                throw t;
            } else if (step.taskType == EXCEPTION_HANDLER) {
                return step.task.apply(t);
            } else {
                step.task.apply(t);
                throw t;
            }
        } catch (Throwable e) {
            throw toRuntimeException(e);
        }
    }

    private RuntimeException toRuntimeException(Throwable t) {
        if (t instanceof RuntimeException) {
            return (RuntimeException) t;
        }
        return new CompletionException(t);
    }

    static class ChainStep {
        final Executor executor;
        final TaskType taskType;
        final CheckedFunction<Object, Object> task;

        ChainStep(Executor executor, TaskType taskType, CheckedFunction<Object, Object> task) {
            this.executor = executor;
            this.taskType = taskType;
            this.task = task;
        }
    }
}
