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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.rico.internal.remoting.communication.commands.Command;
import dev.rico.internal.remoting.communication.codec.CodecConstants;
import org.apiguardian.api.API;
import sun.lwawt.macosx.CocoaConstants;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public abstract class AbstractCommandTranscoder<C extends Command> implements CommandTranscoder<C> {

    private final String type;

    private final Class<C> commandClass;

    public AbstractCommandTranscoder(final String type, Class<C> commandClass) {
        this.type = Assert.requireNonBlank(type, "type");
        this.commandClass = Assert.requireNonNull(commandClass, "commandClass");
    }

    protected abstract void encode(final C command, final JsonObject jsonCommand);

    @Override
    public JsonObject encode(final C command) {
        Assert.requireNonNull(command, "command");
        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(CodecConstants.COMMAND_TYPE_ATTRIBUTE, type);
        jsonCommand.addProperty(CodecConstants.ID_ATTRIBUTE, command.getUniqueIdentifier());
        encode(command, jsonCommand);
        return jsonCommand;
    }

    protected boolean isElementJsonNull(final JsonObject jsonObject, final String jsonElementName) {
        return getElement(jsonObject, jsonElementName).isJsonNull();
    }

    protected String getStringElement(final JsonObject jsonObject, final String jsonElementName) {
        final JsonElement element = getElement(jsonObject, jsonElementName);
        if(element.isJsonNull()) {
            return null;
        }
        return element.getAsString();
    }

    protected int getIntElement(final JsonObject jsonObject, final String jsonElementName) {
        return getElement(jsonObject, jsonElementName).getAsInt();
    }

    private JsonElement getElement(final JsonObject jsonObject, final String jsonElementName) {
        Assert.requireNonNull(jsonObject, "jsonObject");
        Assert.requireNonNull(jsonElementName, "jsonElementName");
        JsonElement element = jsonObject.get(jsonElementName);
        Assert.requireNonNull(element, "element");
        return element;
    }

    public Class<C> getCommandClass() {
        return commandClass;
    }

    public String getType() {
        return type;
    }
}
