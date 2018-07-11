package dev.rico.client.concurrent;

public interface BackgroundTask {

    default String getName() {
        return "";
    }

    default String geDescription() {
        return "";
    }

    void cancel();

    boolean isCancelled();

    boolean isDone();

    boolean isRunning();

}
