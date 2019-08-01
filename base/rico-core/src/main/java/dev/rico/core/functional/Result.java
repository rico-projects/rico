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

import dev.rico.internal.core.functional.Fail;
import dev.rico.internal.core.functional.Sucess;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Wrapper for a result of a functional call (like {@link CheckedFunction}). The result can hold the outcome of a function
 * or an excepton if the function was aborted exeptionally.
 * @param <R> type of the outcome
 */
public interface Result<R> {

    /**
     * Returns the outcome /result of the based functional call or throws an {@link IllegalStateException} if the
     * function was aborded by an exception. Such behavior can easily be checked by calling {@link Result#iSuccessful()}
     * @return the outcome
     * @throws IllegalStateException the exception if the based function was not executed sucessfully
     */
    R getResult() throws IllegalStateException;

    /**
     * Returns true if the based function was executed sucessfully, otherwise false.
     * @return true if the based function was executed sucessfully, otherwise false.
     */
    boolean iSuccessful();

    /**
     * Returns the exception of the based functional call or throws an {@link IllegalStateException} if the
     * function was executed sucessfully. Such behavior can easily be checked by calling {@link Result#iSuccessful()}
     * @return the exception
     * @throws IllegalStateException the exception if the based function was executed sucessfully
     */
    Exception getException() throws IllegalStateException;

    /**
     * Wraps a given {@link CheckedFunction} in a {@link Function} that returns the {@link Result} of the given {@link CheckedFunction}
     * @param function the function
     * @param <A> type of the input parameter
     * @param <B> type of the result
     * @return a {@link Function} that returns the {@link Result} of the given {@link CheckedFunction}
     */
    static <A, B> Function<A, Result<B>> of(final CheckedFunction<A, B> function) {
        return (a) -> {
            try {
                final B result = function.apply(a);
                return new Sucess<>(result);
            } catch (Exception e) {
                return new Fail<>(e);
            }
        };
    }

    /**
     * Wraps a given {@link CheckedSupplier} in a {@link Supplier} that returns the {@link Result} of the given {@link CheckedSupplier}
     * @param supplier the supplier
     * @param <B> type of the result
     * @return a {@link Supplier} that returns the {@link Result} of the given {@link CheckedSupplier}
     */
    static <B> Supplier<Result<B>> of(final CheckedSupplier<B> supplier) {
        return () -> {
            try {
                final B result = supplier.get();
                return new Sucess<>(result);
            } catch (Exception e) {
                return new Fail<>(e);
            }
        };
    }

    /**
     * Wraps a given {@link CheckedFunction} in a {@link Function} that returns the {@link Result} of the given {@link CheckedFunction}
     * @param function the function
     * @param <A> type of the input parameter
     * @param <B> type of the result
     * @return a {@link Function} that returns the {@link Result} of the given {@link CheckedFunction}
     */
    static <A, B> Function<A, ResultWithInput<A, B>> withInput(final CheckedFunction<A, B> function) {
        return (a) -> {
            try {
                final B result = function.apply(a);
                return new Sucess<>(a, result);
            } catch (Exception e) {
                return new Fail<>(a, e);
            }
        };
    }

 }
