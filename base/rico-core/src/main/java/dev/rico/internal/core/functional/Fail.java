package dev.rico.internal.core.functional;

import dev.rico.core.functional.ResultWithInput;
import dev.rico.internal.core.Assert;

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
    public boolean iSuccessful() {
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
}
