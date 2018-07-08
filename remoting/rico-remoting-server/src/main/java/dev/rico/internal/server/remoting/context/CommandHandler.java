package dev.rico.internal.server.remoting.context;

import dev.rico.internal.remoting.communication.commands.Command;

public interface CommandHandler<T extends Command> {

    void handle(T command) throws Exception;

}
