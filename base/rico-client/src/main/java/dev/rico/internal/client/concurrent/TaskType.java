package dev.rico.internal.client.concurrent;

/**
 * Types of tasks used in {@link dev.rico.client.concurrent.TaskChain}.
 */
enum TaskType {
    TASK,
    EXCEPTION_HANDLER,
    FINALLY,
}
