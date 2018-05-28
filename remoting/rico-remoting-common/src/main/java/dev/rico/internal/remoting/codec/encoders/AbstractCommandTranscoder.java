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
import dev.rico.internal.remoting.legacy.communication.Command;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public abstract class AbstractCommandTranscoder<C extends Command> implements CommandTranscoder<C> {

    protected boolean isElementJsonNull(final JsonObject jsonObject, final String jsonElementName) {
        return getElement(jsonObject, jsonElementName).isJsonNull();
    }

    protected String getStringElement(final JsonObject jsonObject, final String jsonElementName) {
        return getElement(jsonObject, jsonElementName).getAsString();
    }

    private JsonElement getElement(final JsonObject jsonObject, final String jsonElementName) {
        Assert.requireNonNull(jsonObject, "jsonObject");
        Assert.requireNonNull(jsonElementName, "jsonElementName");
        JsonElement element = jsonObject.get(jsonElementName);
        Assert.requireNonNull(element, "element");
        return element;
    }
}
