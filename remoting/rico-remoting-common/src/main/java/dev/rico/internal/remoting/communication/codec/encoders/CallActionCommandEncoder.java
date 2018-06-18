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
import dev.rico.internal.remoting.communication.commands.CallActionCommand;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.rico.internal.remoting.communication.commands.CommandConstants;
import org.apiguardian.api.API;

import java.util.Map;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class CallActionCommandEncoder extends AbstractCommandTranscoder<CallActionCommand> {

    public CallActionCommandEncoder() {
        super(CommandConstants.CALL_ACTION_COMMAND_ID, CallActionCommand.class);
    }

    @Override
    public void encode(final CallActionCommand command, final JsonObject jsonCommand) {
        Assert.requireNonNull(command, "command");
        jsonCommand.addProperty(CommandConstants.NAME_ATTRIBUTE, command.getActionName());

        final JsonArray paramArray = new JsonArray();
        for(final Map.Entry<String, Object> paramEntry : command.getParams().entrySet()) {
            final JsonObject paramObject = new JsonObject();
            paramObject.addProperty(CommandConstants.NAME_ATTRIBUTE, paramEntry.getKey());
            paramObject.add(CommandConstants.VALUE_ATTRIBUTE, ValueEncoder.encodeValue(paramEntry.getValue()));
            paramArray.add(paramObject);
        }
        jsonCommand.add(CommandConstants.PARAMS_ATTRIBUTE, paramArray);
    }

    @Override
    public CallActionCommand decode(final JsonObject jsonObject) {
        Assert.requireNonNull(jsonObject, "jsonObject");
        try {
            final CallActionCommand command = new CallActionCommand();
            command.setActionName(getStringElement(jsonObject, CommandConstants.NAME_ATTRIBUTE));

            final JsonArray jsonArray = jsonObject.getAsJsonArray(CommandConstants.PARAMS_ATTRIBUTE);
            if(jsonArray != null) {
                for (final JsonElement jsonElement : jsonArray) {
                    final JsonObject paramObject = jsonElement.getAsJsonObject();
                    command.addParam(getStringElement(paramObject, CommandConstants.NAME_ATTRIBUTE), ValueEncoder.decodeValue(paramObject.get(CommandConstants.VALUE_ATTRIBUTE)));
                }
            }
            return command;
        } catch (final Exception ex) {
            throw new JsonParseException("Illegal JSON detected", ex);
        }
    }
}
