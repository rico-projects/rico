package dev.rico.core.concurrent;

public interface Task {

    default String getName() {
        return "";
    }

    default String geDescription() {
        return "";
    }

    void cancel();

}
