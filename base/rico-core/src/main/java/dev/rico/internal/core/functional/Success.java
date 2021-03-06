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
package dev.rico.internal.core.functional;

import dev.rico.core.functional.CheckedBiFunction;
import dev.rico.core.functional.CheckedConsumer;
import dev.rico.core.functional.CheckedFunction;
import dev.rico.core.functional.CheckedRunnable;
import dev.rico.core.functional.Result;
import dev.rico.core.functional.ResultWithInput;
import dev.rico.internal.core.Assert;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Implementation of a {@link dev.rico.core.functional.Result} that is based
 * on a successfully executed function
 *
 * @param <T> type of the input
 * @param <R> type of the output
 */
public class Success<T, R> implements ResultWithInput<T, R> {

    private final T input;

    private final R result;

    public Success(final T input, final R result) {
        this.input = input;
        this.result = result;
    }

    public Success(final R result) {
        this.input = null;
        this.result = result;
    }

    @Override
    public boolean isSuccessful() {
        return true;
    }

    @Override
    public T getInput() {
        return input;
    }

    @Override
    public Exception getException() {
        return null;
    }

    @Override
    public R getResult() {
        return result;
    }

    @Override
    public R orElse(R value) {
        return result;
    }

    @Override
    public <U> Result<U> map(final CheckedFunction<R, U> mapper) {
        Assert.requireNonNull(mapper, "mapper");
        try {
            return new Success<>(input, mapper.apply(result));
        } catch (Exception e) {
            return new Fail<>(input, e);
        }
    }

    @Override
    public Result<R> recover(CheckedFunction<Exception, R> exceptionHandler) {
        Assert.requireNonNull(exceptionHandler, "exceptionHandler");
        return this;
    }

    @Override
    public ResultWithInput<T, R> recover(CheckedBiFunction<T, Exception, R> exceptionHandler) {
        Assert.requireNonNull(exceptionHandler, "exceptionHandler");
        return this;
    }

    @Override
    public Result<Void> onSuccess(CheckedConsumer<R> consumer) {
        Assert.requireNonNull(consumer, "consumer");
        return map(v -> {
            consumer.accept(v);
            return null;
        });
    }

    @Override
    public Result<Void> onSuccess(CheckedRunnable runnable) {
        Assert.requireNonNull(runnable, "runnable");
        return map(v -> {
            runnable.run();
            return null;
        });
    }

    @Override
    public void onFailure(Consumer<Exception> consumer) {
        Assert.requireNonNull(consumer, "consumer");
        // do nothing
    }

    @Override
    public void onFailure(BiConsumer<T, Exception> consumer) {
        Assert.requireNonNull(consumer, "consumer");
        // do nothing
    }
}

