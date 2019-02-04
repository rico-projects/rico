package dev.rico.core.functional;

import dev.rico.internal.core.functional.Fail;
import dev.rico.internal.core.functional.Sucess;

import java.util.function.Function;

public interface Result<R> {

    R getResult();

    boolean iSuccessful();

    Exception getException();

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
