package dev.rico.internal.client.remoting.communication;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.communication.commands.Command;

public class CommandAndHandler {

    private final Command command;

    private final Runnable handler;

    public CommandAndHandler(Command command, Runnable handler) {
        this.command = Assert.requireNonNull(command, "command");
        this.handler = handler;
    }

    public Command getCommand() {
        return command;
    }

    public Runnable getHandler() {
        return handler;
    }
}
