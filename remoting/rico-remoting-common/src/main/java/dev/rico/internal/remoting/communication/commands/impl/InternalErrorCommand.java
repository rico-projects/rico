package dev.rico.internal.remoting.communication.commands.impl;

import dev.rico.internal.remoting.communication.commands.AbstractCommand;

public class InternalErrorCommand extends AbstractCommand {

    public InternalErrorCommand() {
    }

    public InternalErrorCommand(final String uniqueIdentifier) {
        super(uniqueIdentifier);
    }
}
