package dev.rico.internal.remoting.communication.codec.encoders;

import com.google.gson.JsonObject;
import dev.rico.internal.remoting.communication.commands.impl.CreateBeanTypeCommand;

import static dev.rico.internal.remoting.communication.codec.CodecConstants.*;

public class CreateBeanTypeCommandTranscoder extends AbstractCommandTranscoder<CreateBeanTypeCommand> {

    public CreateBeanTypeCommandTranscoder() {
        super(CREATE_BEAN_TYPE_COMMAND_ID, CreateBeanTypeCommand.class);
    }

    @Override
    public CreateBeanTypeCommand decode(final JsonObject jsonObject) {
        final CreateBeanTypeCommand command = new CreateBeanTypeCommand(getStringElement(jsonObject, ID_ATTRIBUTE));
        command.setBeanType(getStringElement(jsonObject, BEAN_ATTRIBUTE));
        command.setClassName(getStringElement(jsonObject, CLASS_ATTRIBUTE));
        return command;
    }

    @Override
    protected void encode(CreateBeanTypeCommand command, JsonObject jsonCommand) {
        jsonCommand.addProperty(BEAN_ATTRIBUTE, command.getBeanType());
        jsonCommand.addProperty(CLASS_ATTRIBUTE, command.getClassName());
    }
}

