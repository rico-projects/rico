package dev.rico.internal.remoting.communication.merge;

import dev.rico.internal.remoting.communication.commands.impl.*;

import java.util.Objects;

public class CommandMergeUtils {

    public static void checkValueChangedCommand(final ValueChangedCommand command, final DeleteTrigger deleteTrigger, final ValueChangedCommand nextCommand) {
        if (Objects.equals(nextCommand.getBeanId(), command.getBeanId()) && Objects.equals(nextCommand.getPropertyName(), command.getPropertyName())) {
            deleteTrigger.deleteCommand();
        }
    }

    public static void checkListAddCommand(final ListAddCommand command, final DeleteTrigger deleteTrigger, final ListAddCommand nextCommand) {
        if (Objects.equals(nextCommand.getBeanId(), command.getBeanId()) && Objects.equals(nextCommand.getListName(), command.getListName())) {
            final int prefEndIndex = command.getStart() + command.getValues().size();
            if (Objects.equals(prefEndIndex, nextCommand.getStart())) {
                nextCommand.setStart(command.getStart());
                nextCommand.getValues().addAll(0, command.getValues());
                deleteTrigger.deleteCommand();
            }
        }
    }

    public static void checkListReplaceCommand(final ListReplaceCommand command, final DeleteTrigger deleteTrigger, final ListReplaceCommand nextCommand) {
        if (Objects.equals(nextCommand.getBeanId(), command.getBeanId()) && Objects.equals(nextCommand.getListName(), command.getListName())) {
            final int prefEndIndex = command.getStart() + command.getValues().size();
            if (Objects.equals(prefEndIndex, nextCommand.getStart())) {
                nextCommand.setStart(command.getStart());
                nextCommand.getValues().addAll(0, command.getValues());
                deleteTrigger.deleteCommand();
            }
        }
    }

    public static void checkListRemoveCommand(final ListRemoveCommand command, final DeleteTrigger deleteTrigger, final ListRemoveCommand nextCommand) {
        if (Objects.equals(nextCommand.getBeanId(), command.getBeanId()) && Objects.equals(nextCommand.getListName(), command.getListName())) {
            if(Objects.equals(command.getTo(), nextCommand.getFrom())) {
                nextCommand.setFrom(command.getFrom());
                deleteTrigger.deleteCommand();
            }
        }
    }

    @FunctionalInterface
    public interface DeleteTrigger {
        void deleteCommand();
    }

}
