package dev.rico.core.functional;

import dev.rico.internal.core.Assert;

import java.util.function.Function;
import java.util.stream.Stream;

public class Result<T, R> {

    private final Either<Pair<T, R>, Pair<T, Exception>> either;

    private Result(T in, R value) {
        this.either = Either.left(new Pair<>(in, value));
    }

    private Result(T value, Exception e) {
        this.either = Either.right(new Pair<>(value, e));
    }

    public static <T, R> Result<T, R> success(final T in, final R out) {
        return new Result<>(in, out);
    }

    public static <T, R> Result<T, R> fail(final T in, final Exception e) {
        return new Result<>(in, e);
    }

    public T getInput() {
        return either.left().map(l -> l.getKey()).orElseGet(() -> either.getRight().getKey());
    }

    public boolean isSuccessfull() {
        return either.isLeft();
    }

    public R getResult() {
        return either.left().map(l -> l.getValue()).orElseThrow(() -> new IllegalStateException("Result is not successfull"));
    }

    public Exception getError() {
        return either.right().map(r -> r.getValue()).orElseThrow(() -> new IllegalStateException("Result is successfull"));
    }

    static <T, R> Function<T, Result<T, R>> of(final CheckedFunction<T, R> function) {
        Assert.requireNonNull(function, "function");
        return t -> {
            try {
                final R result = function.apply(t);
                return Result.success(t, result);
            } catch (Exception e) {
                return Result.fail(t, e);
            }
        };
    }
}
