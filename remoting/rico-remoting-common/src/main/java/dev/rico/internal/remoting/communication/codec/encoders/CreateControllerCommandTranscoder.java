/*
 * Copyright 2018 Karakun AG.
 * Copyright 2015-2018 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.rico.internal.remoting.communication.codec.encoders;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.communication.codec.CodecConstants;
import dev.rico.internal.remoting.communication.commands.impl.CreateControllerCommand;
import com.google.gson.JsonObject;
import org.apiguardian.api.API;

import static dev.rico.internal.remoting.communication.codec.CodecConstants.*;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class CreateControllerCommandTranscoder extends AbstractCommandTranscoder<CreateControllerCommand> {

    public CreateControllerCommandTranscoder() {
        super(CodecConstants.CREATE_CONTROLLER_COMMAND_ID, CreateControllerCommand.class);
    }

    @Override
    protected void encode(CreateControllerCommand command, JsonObject jsonCommand) {
        Assert.requireNonNull(command, "command");
        Assert.requireNonNull(jsonCommand, "jsonCommand");
        jsonCommand.addProperty(CONTROLLER_ATTRIBUTE, command.getControllerId());
        jsonCommand.addProperty(NAME_ATTRIBUTE, command.getControllerName());
        jsonCommand.addProperty(MODEL_ATTRIBUTE, command.getModelId());
        jsonCommand.addProperty(PARENT_ATTRIBUTE, command.getParentControllerId());
    }

    @Override
    public CreateControllerCommand decode(final JsonObject jsonObject) {
        Assert.requireNonNull(jsonObject, "jsonObject");
        final CreateControllerCommand command = new CreateControllerCommand(getStringElement(jsonObject, ID_ATTRIBUTE));
        command.setControllerId(getStringElement(jsonObject, CONTROLLER_ATTRIBUTE));
        command.setControllerName(getStringElement(jsonObject, NAME_ATTRIBUTE));
        command.setModelId(getStringElement(jsonObject, MODEL_ATTRIBUTE));
        if(jsonObject.has(PARENT_ATTRIBUTE)) {
            command.setParentControllerId(getStringElement(jsonObject, PARENT_ATTRIBUTE));
        }
        return command;
    }
}
