package dev.rico.core.functional;

/**
 * Functional interface like {@link java.util.function.Consumer} that can throw an exception at runtime
 * @param <T> input parameter type
 */
@FunctionalInterface
public interface CheckedConsumer<T> {

    /**
     * Performs this consumer on the given input.
     *
     * @param t the input
     */
    void accept(T t) throws Exception;
}
