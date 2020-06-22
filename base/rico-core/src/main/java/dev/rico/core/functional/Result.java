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
package dev.rico.core.functional;

import dev.rico.internal.core.Assert;
import dev.rico.internal.core.functional.Fail;
import dev.rico.internal.core.functional.Success;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Wrapper for a result of a functional call (like {@link CheckedFunction}). The result can hold the outcome of a function
 * or an exception if the function was aborted exceptionally.
 *
 * @param <R> type of the outcome
 */
public interface Result<R> {

    /**
     * Returns the return value of the based functional call or throws an {@link IllegalStateException} if the
     * function was aborted by an exception.
     * <p>
     * Best practice is to test the state by calling {@link #isSuccessful()}.
     *
     * @return the outcome
     * @throws IllegalStateException the exception if the based function was not executed successfully
     * @see #orElse(Object)
     * @see #map(CheckedFunction)
     * @see #onSuccess(CheckedRunnable)
     * @see #onSuccess(CheckedConsumer)
     */
    R getResult() throws IllegalStateException;

    /**
     * Returns the exception of the based functional call or throws an {@link IllegalStateException} if the
     * function was executed successfully.
     * <p>
     * Best practice is to test the state by calling {@link #isFailed()} ()}.
     *
     * @return the exception
     * @throws IllegalStateException the exception if the based function was executed successfully
     * @see #orElse(Object)
     * @see #onFailure(Consumer)
     * @see #recover(CheckedFunction)
     */
    Exception getException();

    /**
     * @return {@code true} if the based function was executed successfully, otherwise {@code false}.
     */
    boolean isSuccessful();

    /**
     * @return {@code false} if the based function was executed successfully, otherwise {@code true}.
     */
    default boolean isFailed() {
        return !isSuccessful();
    }

    /**
     * Map the result to something else.
     *
     * @param mapper the mapper
     * @param <U>    the return type of the mapper
     * @return a new result
     */
    <U> Result<U> map(CheckedFunction<R, U> mapper);

    /**
     * Allows to recover from an exception.
     *
     * @param exceptionHandler the exception handler
     * @return a new result
     */
    Result<R> recover(CheckedFunction<Exception, R> exceptionHandler);

    /**
     * Returns the value of this if successful or the passed in value otherwise.
     *
     * @param value the value
     * @return a new result
     */
    R orElse(R value);

    /**
     * Consumes the result.
     *
     * @param consumer the consumer
     * @return a new result
     */
    Result<Void> onSuccess(CheckedConsumer<R> consumer);

    /**
     * Executes a runnable if the result is successful.
     *
     * @param runnable the runnable
     * @return a new result
     */
    Result<Void> onSuccess(CheckedRunnable runnable);

    /**
     * Executes an exception handler if the result is failed.
     *
     * @param consumer the exception handler
     */
    void onFailure(Consumer<Exception> consumer);

    /**
     * Returns a successful result with the given value
     *
     * @param value the value of the result
     * @param <B>   the type of the result
     * @return the successful result
     */
    static <B> Result<B> success(final B value) {
        return new Success<>(value);
    }

    /**
     * Returns a failed result with the given exception
     *
     * @param e   the exception of the result
     * @param <B> the type of the result
     * @return the failed result
     */
    static <B> Result<B> fail(final Exception e) {
        return new Fail<>(e);
    }

    /**
     * Wraps a given {@link CheckedSupplier} in a {@link Supplier} that returns the {@link Result} of the given {@link CheckedSupplier}
     *
     * @param supplier the supplier
     * @param <B>      type of the result
     * @return a {@link Supplier} that returns the {@link Result} of the given {@link CheckedSupplier}
     */
    static <B> Result<B> of(final CheckedSupplier<B> supplier) {
        Assert.requireNonNull(supplier, "supplier");
        final CheckedFunction<Void, B> func = a -> supplier.get();
        return of(null, func);
    }

    /**
     * Wraps a given {@link CheckedFunction} in a {@link Function} that returns the {@link Result} of the given {@link CheckedFunction}
     *
     * @param function the function
     * @param <A>      type of the input parameter
     * @param <B>      type of the result
     * @return a {@link Function} that returns the {@link Result} of the given {@link CheckedFunction}
     */
    static <A, B> ResultWithInput<A, B> of(A input, final CheckedFunction<A, B> function) {
        Assert.requireNonNull(function, "function");
        try {
            final B result = function.apply(input);
            return new Success<>(input, result);
        } catch (Exception e) {
            return new Fail<>(input, e);
        }
    }

    /**
     * Wraps a given {@link CheckedFunction} in a {@link Function} that returns the {@link Result} of the given {@link CheckedFunction}
     *
     * @param consumer the consumer
     * @param <A>      type of the input parameter
     * @return a {@link Function} that returns the {@link Result} of the given {@link CheckedFunction}
     */
    static <A> ResultWithInput<A, Void> of(A input, final CheckedConsumer<A> consumer) {
        Assert.requireNonNull(consumer, "consumer");
        return of(input, a -> {
            consumer.accept(a);
            return null;
        });
    }
}
