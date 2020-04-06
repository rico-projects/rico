package dev.rico.internal.remoting.communication.codec.encoders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.rico.internal.remoting.communication.commands.impl.ListReplaceCommand;

import static dev.rico.internal.remoting.communication.codec.CodecConstants.*;

public class ListReplaceCommandTranscoder extends AbstractCommandTranscoder<ListReplaceCommand> {

    public ListReplaceCommandTranscoder() {
        super(LIST_REPLACE_COMMAND_ID, ListReplaceCommand.class);
    }

    @Override
    public ListReplaceCommand decode(final JsonObject jsonObject) {
        ListReplaceCommand command = new ListReplaceCommand(getStringElement(jsonObject, ID_ATTRIBUTE));
        command.setBeanId(getStringElement(jsonObject, BEAN_ATTRIBUTE));
        command.setListName(getStringElement(jsonObject, NAME_ATTRIBUTE));
        command.setStart(getIntElement(jsonObject, FROM_ATTRIBUTE));
        final JsonArray jsonArray = jsonObject.getAsJsonArray(PARAMS_ATTRIBUTE);
        if (jsonArray != null) {
            for (final JsonElement jsonElement : jsonArray) {
                command.getValues().add(ValueEncoder.decodeValue(jsonElement));
            }
        }
        return command;
    }

    @Override
    protected void encode(final ListReplaceCommand command, JsonObject jsonCommand) {
        jsonCommand.addProperty(BEAN_ATTRIBUTE, command.getBeanId());
        jsonCommand.addProperty(NAME_ATTRIBUTE, command.getListName());
        jsonCommand.addProperty(FROM_ATTRIBUTE, command.getStart());
        final JsonArray paramArray = new JsonArray();
        for (Object value : command.getValues()) {
            paramArray.add(ValueEncoder.encodeValue(value));
        }
        jsonCommand.add(PARAMS_ATTRIBUTE, paramArray);
    }
}

