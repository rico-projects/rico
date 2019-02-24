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
import dev.rico.internal.remoting.legacy.communication.PresentationModelDeletedCommand;
import com.google.gson.JsonObject;
import org.apiguardian.api.API;

import static dev.rico.internal.remoting.legacy.communication.CommandConstants.ID;
import static dev.rico.internal.remoting.legacy.communication.CommandConstants.PM_ID;
import static dev.rico.internal.remoting.legacy.communication.CommandConstants.PRESENTATION_MODEL_DELETED_COMMAND_ID;
import static org.apiguardian.api.API.Status.DEPRECATED;

@Deprecated
@API(since = "0.x", status = DEPRECATED)
public class PresentationModelDeletedCommandEncoder implements CommandTranscoder<PresentationModelDeletedCommand> {

    @Override
    public JsonObject encode(final PresentationModelDeletedCommand command) {
        Assert.requireNonNull(command, "command");
        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(ID, PRESENTATION_MODEL_DELETED_COMMAND_ID);
        jsonCommand.addProperty(PM_ID, command.getPmId());
        return jsonCommand;
    }

    @Override
    public PresentationModelDeletedCommand decode(final JsonObject jsonObject) {
        final PresentationModelDeletedCommand command = new PresentationModelDeletedCommand();
        command.setPmId(JsonUtils.getStringElement(jsonObject, PM_ID));
        return command;
    }
}
