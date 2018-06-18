package dev.rico.internal.remoting.communication.codec.encoders;

import com.google.gson.JsonObject;
import dev.rico.internal.remoting.communication.commands.BeanCreatedCommand;
import dev.rico.internal.remoting.communication.commands.CallActionCommand;
import dev.rico.internal.remoting.communication.commands.CommandConstants;

public class  BeanCreatedCommandTranscoder extends AbstractCommandTranscoder<BeanCreatedCommand> {

    public BeanCreatedCommandTranscoder() {
        super(CommandConstants.BEAN_CREATED_COMMAND_ID, BeanCreatedCommand.class);
    }

    @Override
    protected void encode(BeanCreatedCommand command, JsonObject jsonCommand) {

    }

    @Override
    public BeanCreatedCommand decode(JsonObject jsonObject) {
        return null;
    }
}
