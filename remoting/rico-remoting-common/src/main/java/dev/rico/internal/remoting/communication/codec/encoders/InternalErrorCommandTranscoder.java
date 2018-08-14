package dev.rico.internal.remoting.communication.codec.encoders;

import com.google.gson.JsonObject;
import dev.rico.internal.remoting.communication.codec.CodecConstants;
import dev.rico.internal.remoting.communication.commands.impl.DestroyContextCommand;
import dev.rico.internal.remoting.communication.commands.impl.InternalErrorCommand;

import static dev.rico.internal.remoting.communication.codec.CodecConstants.ID_ATTRIBUTE;

public class InternalErrorCommandTranscoder extends AbstractCommandTranscoder<InternalErrorCommand> {


    public InternalErrorCommandTranscoder() {
        super(CodecConstants.INTERNAL_ERROR_COMMAND_ID, InternalErrorCommand.class);
    }

    @Override
    public InternalErrorCommand decode(final JsonObject jsonObject) {
        final InternalErrorCommand command = new InternalErrorCommand(getStringElement(jsonObject, ID_ATTRIBUTE));
        return command;
    }

    @Override
    protected void encode(final InternalErrorCommand command, final JsonObject jsonCommand) {

    }
}

