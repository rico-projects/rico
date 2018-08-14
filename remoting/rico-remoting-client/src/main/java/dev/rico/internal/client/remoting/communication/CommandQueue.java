package dev.rico.internal.client.remoting.communication;

import dev.rico.internal.remoting.communication.commands.Command;
import dev.rico.internal.remoting.communication.commands.impl.ListAddCommand;
import dev.rico.internal.remoting.communication.commands.impl.ListRemoveCommand;
import dev.rico.internal.remoting.communication.commands.impl.ListReplaceCommand;
import dev.rico.internal.remoting.communication.commands.impl.ValueChangedCommand;
import dev.rico.internal.remoting.communication.merge.CommandMergeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CommandQueue {

    private final Queue<CommandAndHandler> commandQueue;

    private final Lock lock;

    public CommandQueue() {
        commandQueue = new LinkedBlockingQueue();
        lock = new ReentrantLock();
    }

    /**
     *
     * @param command
     * @return true is this is the firxyst element in the queue
     */
    public boolean add(final CommandAndHandler command) {
        lock.lock();
        try {
            commandQueue.add(command);
            return (commandQueue.size() == 1);
        } finally {
            lock.unlock();
        }
    }

    public List<CommandAndHandler> getAll() {
        lock.lock();
        try {
            final List<CommandAndHandler> commands = new ArrayList<>();
            while (!commandQueue.isEmpty()) {
                final CommandAndHandler toAdd = commandQueue.poll();
                doPossibleMerge(commands, toAdd);
                commands.add(toAdd);
            }
            return commands;
        } finally {
            lock.unlock();
        }
    }

    private void doPossibleMerge(final List<CommandAndHandler> current, final CommandAndHandler next) {
        final Command nextCommand = next.getCommand();
        if(nextCommand instanceof ValueChangedCommand) {
            ListIterator<CommandAndHandler> iterator = current.listIterator();
            while (iterator.hasNext()) {
                final Command command = iterator.next().getCommand();
                if(command instanceof ValueChangedCommand) {
                    CommandMergeUtils.checkValueChangedCommand((ValueChangedCommand)command, () -> iterator.remove(), (ValueChangedCommand)nextCommand);
                }
            }
        } else if(nextCommand instanceof ListAddCommand) {
            ListIterator<CommandAndHandler> iterator = current.listIterator();
            while (iterator.hasNext()) {
                final Command command = iterator.next().getCommand();
                if(command instanceof ListAddCommand) {
                    CommandMergeUtils.checkListAddCommand((ListAddCommand)command, () -> iterator.remove(), (ListAddCommand)nextCommand);
                }
            }
        } else if(nextCommand instanceof ListReplaceCommand) {
            ListIterator<CommandAndHandler> iterator = current.listIterator();
            while (iterator.hasNext()) {
                final Command command = iterator.next().getCommand();
                if(command instanceof ListReplaceCommand) {
                    CommandMergeUtils.checkListReplaceCommand((ListReplaceCommand)command, () -> iterator.remove(), (ListReplaceCommand)nextCommand);
                }
            }
        } else if(nextCommand instanceof ListRemoveCommand) {
            ListIterator<CommandAndHandler> iterator = current.listIterator();
            while (iterator.hasNext()) {
                final Command command = iterator.next().getCommand();
                if(command instanceof ListRemoveCommand) {
                    CommandMergeUtils.checkListRemoveCommand((ListRemoveCommand)command, () -> iterator.remove(), (ListRemoveCommand)nextCommand);
                }
            }
        }
    }

}
