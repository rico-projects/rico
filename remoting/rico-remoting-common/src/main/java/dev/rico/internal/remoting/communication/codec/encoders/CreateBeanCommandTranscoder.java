package dev.rico.internal.remoting.communication.codec.encoders;

import com.google.gson.JsonObject;
import dev.rico.internal.remoting.communication.codec.CodecConstants;
import dev.rico.internal.remoting.communication.commands.impl.CreateBeanCommand;

import static dev.rico.internal.remoting.communication.codec.CodecConstants.ID_ATTRIBUTE;

public class CreateBeanCommandTranscoder extends AbstractCommandTranscoder<CreateBeanCommand> {

    public CreateBeanCommandTranscoder() {
        super(CodecConstants.CREATE_BEAN_COMMAND_ID, CreateBeanCommand.class);
    }

    @Override
    protected void encode(CreateBeanCommand command, JsonObject jsonCommand) {
        jsonCommand.addProperty(CodecConstants.BEAN_ATTRIBUTE, command.getBeanId());
        jsonCommand.addProperty(CodecConstants.CLASS_ATTRIBUTE, command.getClassId());
    }

    @Override
    public CreateBeanCommand decode(JsonObject jsonObject) {
        final CreateBeanCommand command = new CreateBeanCommand(getStringElement(jsonObject, ID_ATTRIBUTE));
        command.setBeanId(getStringElement(jsonObject, CodecConstants.BEAN_ATTRIBUTE));
        command.setClassId(getStringElement(jsonObject, CodecConstants.CLASS_ATTRIBUTE));
        return command;
    }
}
