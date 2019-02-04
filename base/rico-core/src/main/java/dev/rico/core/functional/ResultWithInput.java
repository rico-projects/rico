package dev.rico.core.functional;

public interface ResultWithInput<V, R> extends Result<R> {

    V getInput();
}
