package dev.rico.internal.remoting.communication.commands;

import dev.rico.internal.core.Assert;

public class ErrorResponseCommand extends AbstractCommand {

    private final String requestIdentifier;

    private String message;

    public ErrorResponseCommand(final String requestIdentifier) {
        this(requestIdentifier, ID_COUNTER.incrementAndGet() + "");
    }

    public ErrorResponseCommand(final String requestIdentifier, final String responseIdentifer) {
        super(responseIdentifer);
        this.requestIdentifier = Assert.requireNonBlank(requestIdentifier, "requestIdentifier");
        this.message = message;
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
