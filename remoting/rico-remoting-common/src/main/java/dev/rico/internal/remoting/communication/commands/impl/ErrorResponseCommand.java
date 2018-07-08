package dev.rico.internal.remoting.communication.commands.impl;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.communication.commands.AbstractCommand;

public class ErrorResponseCommand extends AbstractCommand {

    private String requestIdentifier;

    private String message;

    public ErrorResponseCommand() {
    }

    public ErrorResponseCommand(String uniqueIdentifier) {
        super(uniqueIdentifier);
    }

    public void setRequestIdentifier(String requestIdentifier) {
        this.requestIdentifier = requestIdentifier;
    }

    public String getRequestIdentifier() {
        return requestIdentifier;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
