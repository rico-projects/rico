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

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ValueEncoder {

    private ValueEncoder() {}

    public static JsonElement encodeValue(final Object value) {
        if(value == null) {
            return JsonNull.INSTANCE;
        }
        if (value instanceof String) {
            return new JsonPrimitive((String) value);
        }
        if (value instanceof Number) {
            return new JsonPrimitive((Number) value);
        }
        if (value instanceof Boolean) {
            return new JsonPrimitive((Boolean) value);
        }
        throw new JsonParseException("Only String, Number, and Boolean are allowed currently");
    }

    public static Object decodeValue(final JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return null;
        }
        if (! jsonElement.isJsonPrimitive()) {
            throw new JsonParseException("Illegal JSON detected");
        }
        final JsonPrimitive value = (JsonPrimitive) jsonElement;

        if (value.isString()) {
            return value.getAsString();
        } else if (value.isBoolean()) {
            return value.getAsBoolean();
        } else if (value.isNumber()) {
            return value.getAsNumber();
        }
        throw new JsonParseException("Currently only String, Boolean, or Number are allowed as primitives");
    }
}
