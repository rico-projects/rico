package dev.rico.internal.remoting.communication.codec.encoders;

import com.google.gson.JsonObject;
import dev.rico.internal.remoting.communication.codec.CodecConstants;
import dev.rico.internal.remoting.communication.commands.impl.DestroyContextCommand;
import dev.rico.internal.remoting.communication.commands.impl.ErrorResponseCommand;

import static dev.rico.internal.remoting.communication.codec.CodecConstants.ID_ATTRIBUTE;

public class ErrorResponseCommandTranscoder extends AbstractCommandTranscoder<ErrorResponseCommand> {

    public ErrorResponseCommandTranscoder() {
        super(CodecConstants.ERROR_RESPONSE_COMMAND_ID, ErrorResponseCommand.class);
    }

    @Override
    public ErrorResponseCommand decode(final JsonObject jsonObject) {
        ErrorResponseCommand command = new ErrorResponseCommand(getStringElement(jsonObject, ID_ATTRIBUTE));
        command.setRequestIdentifier(getStringElement(jsonObject, CodecConstants.REQUEST_ATTRIBUTE));
        command.setMessage(getStringElement(jsonObject, CodecConstants.MESSAGE_ATTRIBUTE));
        return command;
    }

    @Override
    protected void encode(ErrorResponseCommand command, JsonObject jsonCommand) {
        jsonCommand.addProperty(CodecConstants.REQUEST_ATTRIBUTE, command.getRequestIdentifier());
        jsonCommand.addProperty(CodecConstants.MESSAGE_ATTRIBUTE, command.getMessage());
    }
}

