package dev.rico.core.functional;

/**
 * Functional interface like {@link Runnable} that can throw an exception at runtime
 */
@FunctionalInterface
public interface CheckedRunnable {

    /**
     * Gets a result.
     *
     * @throws Exception if the handling of the supplier throws an exception
     */
    void run() throws Exception;
}
