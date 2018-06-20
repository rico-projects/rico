package dev.rico.internal.client.concurrent;

public class TaskHelper {

    private static final TaskHelper INSTANCE = new TaskHelper();

    private final ThreadLocal<String> localName;

    private final ThreadLocal<String> localDescription;

    private TaskHelper() {
        this.localName = new ThreadLocal<>();
        this.localDescription = new ThreadLocal<>();
    }

    public void resetCurrent() {
        setTaskName("");
        setTaskDescription("");
    }

    public void setTaskName(final String name) {
        localName.set(name);
    }

    public void setTaskDescription(final String description) {
        localDescription.set(description);
    }

    public String getCurrentName() {
        return localName.get();
    }

    public String getCurrentDescription() {
        return localDescription.get();
    }

    public static TaskHelper getInstance() {
        return INSTANCE;
    }
}
