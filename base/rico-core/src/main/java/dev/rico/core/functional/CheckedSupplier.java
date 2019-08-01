package dev.rico.core.functional;

/**
 * Functional interface like {@link java.util.function.Supplier} that can throw an exception at runtime
 * @param <T> output parameter type
 */
@FunctionalInterface
public interface CheckedSupplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     * @throws Exception if the handling of the supplier throws an exception
     */
    T get();
}
