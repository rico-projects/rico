/*
 * Copyright 2018-2019 Karakun AG.
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
import dev.rico.internal.remoting.codec.JsonUtils;
import dev.rico.internal.remoting.legacy.communication.ChangeAttributeMetadataCommand;
import com.google.gson.JsonObject;
import org.apiguardian.api.API;

import static dev.rico.internal.remoting.legacy.communication.CommandConstants.ATTRIBUTE_ID;
import static dev.rico.internal.remoting.legacy.communication.CommandConstants.CHANGE_ATTRIBUTE_METADATA_COMMAND_ID;
import static dev.rico.internal.remoting.legacy.communication.CommandConstants.ID;
import static dev.rico.internal.remoting.legacy.communication.CommandConstants.NAME;
import static dev.rico.internal.remoting.legacy.communication.CommandConstants.VALUE;
import static org.apiguardian.api.API.Status.DEPRECATED;

@Deprecated
@API(since = "0.x", status = DEPRECATED)
public class ChangeAttributeMetadataCommandEncoder implements CommandTranscoder<ChangeAttributeMetadataCommand> {

    @Override
    public JsonObject encode(final ChangeAttributeMetadataCommand command) {
        Assert.requireNonNull(command, "command");
        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(ID, CHANGE_ATTRIBUTE_METADATA_COMMAND_ID);
        jsonCommand.addProperty(ATTRIBUTE_ID, command.getAttributeId());
        jsonCommand.addProperty(NAME, command.getMetadataName());
        jsonCommand.add(VALUE, JsonUtils.encodeValue(command.getValue()));
        return jsonCommand;
    }

    @Override
    public ChangeAttributeMetadataCommand decode(final JsonObject jsonObject) {
        final ChangeAttributeMetadataCommand command = new ChangeAttributeMetadataCommand();
        command.setAttributeId(JsonUtils.getStringElement(jsonObject, ATTRIBUTE_ID));
        command.setMetadataName(JsonUtils.getStringElement(jsonObject, NAME));
        command.setValue(JsonUtils.decodeValue(jsonObject.get(VALUE)));
        return command;
    }
}
