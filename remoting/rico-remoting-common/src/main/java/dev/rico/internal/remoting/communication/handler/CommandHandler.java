package dev.rico.internal.remoting.communication.handler;

import dev.rico.internal.remoting.communication.commands.Command;

@FunctionalInterface
public interface CommandHandler<T extends Command> {

    void onCommand(T command) throws Exception;

}
