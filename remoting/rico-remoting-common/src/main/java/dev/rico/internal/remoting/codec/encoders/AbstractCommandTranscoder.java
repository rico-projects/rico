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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.codec.JsonPrimitiveTypes;
import dev.rico.internal.remoting.legacy.communication.Command;
import org.apiguardian.api.API;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static dev.rico.internal.remoting.legacy.communication.CommandConstants.NAME;
import static dev.rico.internal.remoting.legacy.communication.CommandConstants.TYPE;
import static dev.rico.internal.remoting.legacy.communication.CommandConstants.VALUE;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public abstract class AbstractCommandTranscoder<C extends Command> implements CommandTranscoder<C> {

    protected boolean isElementJsonNull(final JsonObject jsonObject, final String jsonElementName) {
        return getElement(jsonObject, jsonElementName).isJsonNull();
    }

    protected JsonArray convertToJsonObject(final Map<String, Serializable> map) {
        Assert.requireNonNull(map, "map");
        final JsonArray array = new JsonArray();
        map.keySet().forEach(key -> {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(NAME, key);
            final Object value = map.get(key);
            if(value == null) {
                //For NULL we do not care about the type
                jsonObject.addProperty(TYPE, JsonPrimitiveTypes.STRING.getType());
                jsonObject.add(VALUE, JsonNull.INSTANCE);
            } else if(value instanceof BigDecimal) {
                jsonObject.addProperty(TYPE, JsonPrimitiveTypes.BIG_DECIMAL.getType());
                jsonObject.add(VALUE, new JsonPrimitive((BigDecimal) value));
            } else if(value instanceof BigInteger) {
                jsonObject.addProperty(TYPE, JsonPrimitiveTypes.BIG_INTEGER.getType());
                jsonObject.add(VALUE, new JsonPrimitive((BigInteger) value));
            } else if(value instanceof Boolean) {
                jsonObject.addProperty(TYPE, JsonPrimitiveTypes.BOOLEAN.getType());
                jsonObject.add(VALUE, new JsonPrimitive((Boolean) value));
            } else if(value instanceof Byte) {
                jsonObject.addProperty(TYPE, JsonPrimitiveTypes.BYTE.getType());
                jsonObject.add(VALUE, new JsonPrimitive((Byte) value));
            } else if(value instanceof Character) {
                jsonObject.addProperty(TYPE, JsonPrimitiveTypes.CHARACTER.getType());
                jsonObject.add(VALUE, new JsonPrimitive((Character) value));
            } else if(value instanceof Double) {
                jsonObject.addProperty(TYPE, JsonPrimitiveTypes.DOUBLE.getType());
                jsonObject.add(VALUE, new JsonPrimitive((Double) value));
            } else if(value instanceof Float) {
                jsonObject.addProperty(TYPE, JsonPrimitiveTypes.FLOAT.getType());
                jsonObject.add(VALUE, new JsonPrimitive((Float) value));
            } else if(value instanceof Integer) {
                jsonObject.addProperty(TYPE, JsonPrimitiveTypes.INT.getType());
                jsonObject.add(VALUE, new JsonPrimitive((Integer) value));
            } else if(value instanceof Long) {
                jsonObject.addProperty(TYPE, JsonPrimitiveTypes.LONG.getType());
                jsonObject.add(VALUE, new JsonPrimitive((Long) value));
            } else if(value instanceof Short) {
                jsonObject.addProperty(TYPE, JsonPrimitiveTypes.SHORT.getType());
                jsonObject.add(VALUE, new JsonPrimitive((Short) value));
            } else if(value instanceof String) {
                jsonObject.addProperty(TYPE, JsonPrimitiveTypes.STRING.getType());
                jsonObject.add(VALUE, new JsonPrimitive((String) value));
            } else {
               throw new IllegalArgumentException("Can not handle value of type '" + value.getClass() + "'");
            }
            array.add(jsonObject);
        });
        return array;
    }

    protected Map<String, Serializable> getAsMap(final JsonObject jsonObject, final String jsonElementName) {
        final Map<String, Serializable> result = new HashMap<>();
        final JsonArray array = getElement(jsonObject, jsonElementName).getAsJsonArray();
        array.forEach(i -> {
            final JsonObject element = i.getAsJsonObject();
            final String key = getStringElement(element, NAME);
            final JsonPrimitive value = getElement(element, VALUE).getAsJsonPrimitive();
            final String typeName = getStringElement(element, TYPE);
            final JsonPrimitiveTypes type = JsonPrimitiveTypes.ofType(typeName);
            result.put(key, type.getValueOfElement(value));
        });
        return result;
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
