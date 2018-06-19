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
import dev.rico.internal.remoting.communication.commands.CommandConstants;
import dev.rico.internal.remoting.communication.commands.CreateControllerCommand;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class CreateControllerCommandTranscoder extends AbstractCommandTranscoder<CreateControllerCommand> {

    public CreateControllerCommandTranscoder() {
        super(CommandConstants.CREATE_CONTROLLER_COMMAND_ID, CreateControllerCommand.class);
    }

    @Override
    protected void encode(CreateControllerCommand command, JsonObject jsonCommand) {
        jsonCommand.addProperty(CommandConstants.CONTROLLER_ID_ATTRIBUTE, command.getParentControllerId());
        jsonCommand.addProperty(CommandConstants.NAME_ATTRIBUTE, command.getControllerName());
    }

    @Override
    public CreateControllerCommand decode(final JsonObject jsonObject) {
        Assert.requireNonNull(jsonObject, "jsonObject");
        try {
            final CreateControllerCommand command = new CreateControllerCommand();
            if(jsonObject.has(CommandConstants.CONTROLLER_ID_ATTRIBUTE) && !isElementJsonNull(jsonObject, CommandConstants.CONTROLLER_ID_ATTRIBUTE)) {
                command.setParentControllerId(getStringElement(jsonObject, CommandConstants.CONTROLLER_ID_ATTRIBUTE));
            }
            command.setControllerName(getStringElement(jsonObject, CommandConstants.NAME_ATTRIBUTE));
            return command;
        } catch (final Exception ex) {
            throw new JsonParseException("Illegal JSON detected", ex);
        }
    }
}
