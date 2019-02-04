package dev.rico.internal.core.functional;

import dev.rico.core.functional.ResultWithInput;

public class Sucess<T, R> implements ResultWithInput<T, R> {

    private final T input;

    private final R result;

    public Sucess(final T input, final R result) {
        this.input = input;
        this.result = result;
    }

    public Sucess(final R result) {
        this.input = null;
        this.result = result;
    }

    @Override
    public boolean iSuccessful() {
        return true;
    }

    @Override
    public T getInput() {
        return input;
    }

    @Override
    public Exception getException() {
        throw new IllegalStateException("No exception since call was sucessfull");
    }

    @Override
    public R getResult() {
        return result;
    }
}

