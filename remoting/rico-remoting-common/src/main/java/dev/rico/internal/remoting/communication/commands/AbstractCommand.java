package dev.rico.internal.remoting.communication.commands;

import dev.rico.internal.core.Assert;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractCommand implements Command {

    private final String uniqueIdentifier;

    protected final static AtomicLong ID_COUNTER = new AtomicLong();

    public AbstractCommand() {
        this(ID_COUNTER.incrementAndGet() + "");
    }

    public AbstractCommand(final String uniqueIdentifier) {
        this.uniqueIdentifier = Assert.requireNonBlank(uniqueIdentifier, "uniqueIdentifier");
    }

    @Override
    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractCommand that = (AbstractCommand) o;
        return Objects.equals(uniqueIdentifier, that.uniqueIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueIdentifier);
    }
}
