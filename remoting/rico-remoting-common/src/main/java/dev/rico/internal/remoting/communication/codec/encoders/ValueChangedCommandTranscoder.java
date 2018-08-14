package dev.rico.internal.remoting.communication.codec.encoders;

import com.google.gson.JsonObject;
import dev.rico.internal.remoting.communication.codec.CodecConstants;
import dev.rico.internal.remoting.communication.commands.impl.DestroyContextCommand;
import dev.rico.internal.remoting.communication.commands.impl.ValueChangedCommand;

import static dev.rico.internal.remoting.communication.codec.CodecConstants.*;

public class ValueChangedCommandTranscoder extends AbstractCommandTranscoder<ValueChangedCommand> {


    public ValueChangedCommandTranscoder() {
        super(CodecConstants.VALUE_CHANGED_COMMAND_ID, ValueChangedCommand.class);
    }

    @Override
    public ValueChangedCommand decode(final JsonObject jsonObject) {
        ValueChangedCommand command = new ValueChangedCommand(getStringElement(jsonObject, ID_ATTRIBUTE));
        command.setBeanId(getStringElement(jsonObject, BEAN_ATTRIBUTE));
        command.setPropertyName(getStringElement(jsonObject, NAME_ATTRIBUTE));
        command.setNewValue(ValueEncoder.decodeValue(jsonObject.get(VALUE_ATTRIBUTE)));
        return command;
    }

    @Override
    protected void encode(ValueChangedCommand command, JsonObject jsonCommand) {
        jsonCommand.addProperty(BEAN_ATTRIBUTE, command.getBeanId());
        jsonCommand.addProperty(NAME_ATTRIBUTE, command.getPropertyName());
        jsonCommand.add(VALUE_ATTRIBUTE, ValueEncoder.encodeValue(command.getNewValue()));
    }
}

