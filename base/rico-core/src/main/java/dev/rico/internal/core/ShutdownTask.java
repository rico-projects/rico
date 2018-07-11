package dev.rico.internal.core;

@FunctionalInterface
public interface ShutdownTask {

    void run() throws Exception;
}
