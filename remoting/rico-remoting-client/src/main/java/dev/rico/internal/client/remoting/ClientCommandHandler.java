package dev.rico.internal.client.remoting;

import dev.rico.internal.remoting.communication.commands.Command;

public interface ClientCommandHandler {

    void handleResponseCommand(final Command command);
}
