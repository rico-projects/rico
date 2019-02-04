package dev.rico.core.functional;

public interface CheckedFunction<T, R> {

    R apply(T t) throws Exception;

}
