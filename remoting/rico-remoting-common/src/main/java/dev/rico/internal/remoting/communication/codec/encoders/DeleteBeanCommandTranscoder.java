package dev.rico.internal.remoting.communication.codec.encoders;

import com.google.gson.JsonObject;
import dev.rico.internal.remoting.communication.commands.impl.DeleteBeanCommand;

import static dev.rico.internal.remoting.communication.codec.CodecConstants.BEAN_ATTRIBUTE;
import static dev.rico.internal.remoting.communication.codec.CodecConstants.BEAN_REMOVED_COMMAND_ID;
import static dev.rico.internal.remoting.communication.codec.CodecConstants.ID_ATTRIBUTE;

public final class DeleteBeanCommandTranscoder extends AbstractCommandTranscoder<DeleteBeanCommand> {

    public DeleteBeanCommandTranscoder() {
        super(BEAN_REMOVED_COMMAND_ID, DeleteBeanCommand.class);
    }

    @Override
    public DeleteBeanCommand decode(final JsonObject jsonObject) {
        DeleteBeanCommand command = new DeleteBeanCommand(getStringElement(jsonObject, ID_ATTRIBUTE));
        command.setBeanId(getStringElement(jsonObject, BEAN_ATTRIBUTE));
        return command;
    }

    @Override
    protected void encode(final DeleteBeanCommand command, JsonObject jsonCommand) {
        jsonCommand.addProperty(BEAN_ATTRIBUTE, command.getBeanId());
    }
}

