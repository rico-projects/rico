package dev.rico.core.functional;

import dev.rico.internal.core.Assert;

import java.util.function.Function;

@FunctionalInterface
public interface CheckedFunction<T, R> {

    R apply(T t) throws Exception;

    static <T, R> Function<T, R> of(final CheckedFunction<T, R> function) {
        return of(function, e -> {throw new RuntimeException(e);});
    }

    static <T, R> Function<T, R> of(final CheckedFunction<T, R> function, final Function<Exception, R> onException) {
        Assert.requireNonNull(function, "function");
        Assert.requireNonNull(onException, "onException");
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception e) {
                return onException.apply(e);
            }
        };
    }
}
