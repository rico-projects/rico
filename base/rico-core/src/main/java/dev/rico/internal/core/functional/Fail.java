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

import dev.rico.core.functional.CheckedConsumer;
import dev.rico.core.functional.CheckedFunction;
import dev.rico.core.functional.CheckedRunnable;
import dev.rico.core.functional.Result;
import dev.rico.core.functional.ResultWithInput;
import dev.rico.internal.core.Assert;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Implementation of a {@link dev.rico.core.functional.Result} that is based
 * on a not sucessfully executed function
 * @param <T> type of the input
 * @param <R> type of the output
 */
public class Fail<T, R> implements ResultWithInput<T, R> {

    private final T input;

    private final Exception exception;

    public Fail(final T input, final Exception exception) {
        this.input = input;
        this.exception = Assert.requireNonNull(exception, "exception");
    }

    public Fail(final Exception exception) {
        this.input = null;
        this.exception = Assert.requireNonNull(exception, "exception");
    }

    @Override
    public boolean isSuccessful() {
        return false;
    }

    @Override
    public T getInput() {
        return input;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public R getResult() {
        throw new IllegalStateException("No result since call failed!");
    }

    @Override
    public R orElseGet(Supplier<R> supplier) {
        Assert.requireNonNull(supplier, "supplier");
        return supplier.get();
    }

    @Override
    public R orElse(R value) {
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> Result<U> map(final CheckedFunction<R, U> mapper) {
        Assert.requireNonNull(mapper, "mapper");
        return (Result<U>) this;
    }

    @Override
    public Result<R> recover(CheckedFunction<Exception, R> exceptionHandler) {
        Assert.requireNonNull(exceptionHandler, "exceptionHandler");
        try {
            return new Success<>(exceptionHandler.apply(exception));
        } catch (Exception e) {
            return new Fail<>(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Result<Void> onSuccess(CheckedConsumer<R> consumer) {
        Assert.requireNonNull(consumer, "consumer");
        return (Result<Void>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Result<Void> onSuccess(CheckedRunnable runnable) {
        Assert.requireNonNull(runnable, "runnable");
        return (Result<Void>) this;
    }

    @Override
    public void onFailure(Consumer<Exception> consumer) {
        Assert.requireNonNull(consumer, "consumer");
        consumer.accept(exception);
    }
}
