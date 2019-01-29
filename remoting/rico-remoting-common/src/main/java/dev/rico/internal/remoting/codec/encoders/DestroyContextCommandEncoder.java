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
import dev.rico.internal.remoting.commands.DestroyContextCommand;
import com.google.gson.JsonObject;
import org.apiguardian.api.API;

import static dev.rico.internal.remoting.legacy.communication.CommandConstants.DESTROY_CONTEXT_COMMAND_ID;
import static dev.rico.internal.remoting.legacy.communication.CommandConstants.ID;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class DestroyContextCommandEncoder extends AbstractCommandTranscoder<DestroyContextCommand> {
    @Override
    public JsonObject encode(final DestroyContextCommand command) {
        Assert.requireNonNull(command, "command");
        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(ID, DESTROY_CONTEXT_COMMAND_ID);
        return jsonCommand;
    }

    @Override
    public DestroyContextCommand decode(final JsonObject jsonObject) {
        return new DestroyContextCommand();
    }
}
