package dev.rico.internal.remoting.communication.codec.encoders;

import com.google.gson.JsonObject;
import dev.rico.internal.remoting.communication.commands.impl.ListRemoveCommand;

import static dev.rico.internal.remoting.communication.codec.CodecConstants.*;

public class ListRemoveCommandTranscoder extends AbstractCommandTranscoder<ListRemoveCommand> {

    public ListRemoveCommandTranscoder() {
        super(LIST_REMOVE_COMMAND_ID, ListRemoveCommand.class);
    }

    @Override
    public ListRemoveCommand decode(final JsonObject jsonObject) {
        ListRemoveCommand command = new ListRemoveCommand(getStringElement(jsonObject, ID_ATTRIBUTE));
        command.setBeanId(getStringElement(jsonObject, BEAN_ATTRIBUTE));
        command.setListName(getStringElement(jsonObject, NAME_ATTRIBUTE));
        command.setFrom(getIntElement(jsonObject, FROM_ATTRIBUTE));
        command.setTo(getIntElement(jsonObject, TO_ATTRIBUTE));
        return command;
    }

    @Override
    protected void encode(final ListRemoveCommand command, JsonObject jsonCommand) {
        jsonCommand.addProperty(BEAN_ATTRIBUTE, command.getBeanId());
        jsonCommand.addProperty(NAME_ATTRIBUTE, command.getListName());
        jsonCommand.addProperty(FROM_ATTRIBUTE, command.getFrom());
        jsonCommand.addProperty(TO_ATTRIBUTE, command.getTo());
    }
}
