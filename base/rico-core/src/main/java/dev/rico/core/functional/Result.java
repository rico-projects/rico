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
     * Returns the outcome /result of the based functional call or throws an {@link IllegalStateException} if the
     * function was aborted by an exception. Such behavior can easily be checked by calling {@link Result#isSuccessful()}
     *
     * @return the outcome
     * @throws IllegalStateException the exception if the based function was not executed successfully
     */
    R getResult() throws IllegalStateException;

    /**
     * Returns true if the based function was executed successfully, otherwise false.
     *
     * @return true if the based function was executed successfully, otherwise false.
     */
    boolean isSuccessful();

    /**
     * Returns false if the based function was executed successfully, otherwise true.
     *
     * @return false if the based function was executed successfully, otherwise true.
     */
    default boolean isFailed() {
        return !isSuccessful();
    }

    /**
     * Returns the exception of the based functional call or {@code null} if the
     * function was executed successfully. Such behavior can easily be checked by calling {@link Result#isSuccessful()}
     *
     * @return the exception or {@code null}
     */
    Exception getException();

    /**
     * Map the result to something else.
     *
     * @param mapper the mapper
     * @param <U>    the return type of the mapper
     * @return a new result
     */
    <U> Result<U> map(CheckedFunction<R, U> mapper);

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
     * Wraps a given {@link CheckedFunction} in a {@link Function} that returns the {@link Result} of the given {@link CheckedFunction}
     *
     * @param function the function
     * @param <A>      type of the input parameter
     * @param <B>      type of the result
     * @return a {@link Function} that returns the {@link Result} of the given {@link CheckedFunction}
     */
    static <A, B> Function<A, Result<B>> of(final CheckedFunction<A, B> function) {
        Assert.requireNonNull(function, "function");
        return (a) -> {
            try {
                final B result = function.apply(a);
                return new Success<>(result);
            } catch (Exception e) {
                return new Fail<>(e);
            }
        };
    }

    /**
     * Wraps a given {@link CheckedSupplier} in a {@link Supplier} that returns the {@link Result} of the given {@link CheckedSupplier}
     *
     * @param supplier the supplier
     * @param <B>      type of the result
     * @return a {@link Supplier} that returns the {@link Result} of the given {@link CheckedSupplier}
     */
    static <B> Supplier<Result<B>> of(final CheckedSupplier<B> supplier) {
        Assert.requireNonNull(supplier, "supplier");
        return () -> {
            try {
                final B result = supplier.get();
                return new Success<>(result);
            } catch (Exception e) {
                return new Fail<>(e);
            }
        };
    }

    /**
     * Wraps a given {@link CheckedFunction} in a {@link Function} that returns the {@link Result} of the given {@link CheckedFunction}
     *
     * @param function the function
     * @param <A>      type of the input parameter
     * @param <B>      type of the result
     * @return a {@link Function} that returns the {@link Result} of the given {@link CheckedFunction}
     */
    static <A, B> Function<A, ResultWithInput<A, B>> withInput(final CheckedFunction<A, B> function) {
        Assert.requireNonNull(function, "function");
        return (a) -> {
            try {
                final B result = function.apply(a);
                return new Success<>(a, result);
            } catch (Exception e) {
                return new Fail<>(a, e);
            }
        };
    }

    /**
     * Wraps a given {@link CheckedConsumer} in a {@link Function} that returns the {@link Result} of the
     * given {@link CheckedConsumer}. Since an {@link CheckedConsumer} has no return value the {@link Result} is
     * defined as {@code Result<Void>} and will contain a {@code null} value as result value.
     *
     * @param consumer the consumer
     * @param <A>      type of the input parameter
     * @return a {@link Function} that returns the {@link Result} of the given {@link CheckedConsumer}
     */
    static <A> Function<A, Result<Void>> ofConsumer(final CheckedConsumer<A> consumer) {
        Assert.requireNonNull(consumer, "consumer");
        return (a) -> {
            try {
                consumer.accept(a);
                return new Success<>(null);
            } catch (Exception e) {
                return new Fail<>(e);
            }
        };
    }
}
