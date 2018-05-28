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
package dev.rico.internal.remoting.codec.encoders;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.commands.DestroyControllerCommand;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.apiguardian.api.API;

import static dev.rico.internal.remoting.legacy.communication.CommandConstants.CONTROLLER_ID;
import static dev.rico.internal.remoting.legacy.communication.CommandConstants.DESTROY_CONTROLLER_COMMAND_ID;
import static dev.rico.internal.remoting.legacy.communication.CommandConstants.ID;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class DestroyControllerCommandEncoder extends AbstractCommandTranscoder<DestroyControllerCommand> {

    @Override
    public JsonObject encode(final DestroyControllerCommand command) {
        Assert.requireNonNull(command, "command");
        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(CONTROLLER_ID, command.getControllerId());
        jsonCommand.addProperty(ID, DESTROY_CONTROLLER_COMMAND_ID);
        return jsonCommand;
    }

    @Override
    public DestroyControllerCommand decode(final JsonObject jsonObject) {
        Assert.requireNonNull(jsonObject, "jsonObject");
        try {
            final DestroyControllerCommand command = new DestroyControllerCommand();
            command.setControllerId(getStringElement(jsonObject, CONTROLLER_ID));
            return command;
        } catch (final Exception ex) {
            throw new JsonParseException("Illegal JSON detected", ex);
        }
    }
}
