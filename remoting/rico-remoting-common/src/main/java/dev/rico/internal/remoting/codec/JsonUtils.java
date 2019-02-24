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
package dev.rico.internal.remoting.codec;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.ReflectionHelper;
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

/**
 * Some helper classes since GSON uses {@link com.google.gson.internal.LazilyParsedNumber} internally.
 */
@API(since = "0.x", status = INTERNAL)
public class JsonUtils {

    public static Number convert(final Class<?> neededType, final Object value) {
        Assert.requireNonNull(neededType, "neededType");
        if(!ReflectionHelper.isNumber(neededType)) {
            throw new IllegalArgumentException("given type is not a number type: " + neededType.getSimpleName());
        }
        if(value == null && ReflectionHelper.isPrimitiveNumber(neededType)) {
            throw new IllegalArgumentException("null can not be converted for a primitive type");
        }

        if(value == null) {
            return null;
        }

        if(!Number.class.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Given value is not a number! Type " + value.getClass().getSimpleName());
        }
        final Number numberValue = (Number) value;
        if (neededType.equals(Integer.class) || neededType.equals(Integer.TYPE)) {
            return numberValue.intValue();
        } else if (neededType.equals(Long.class) || neededType.equals(Long.TYPE)) {
            return numberValue.longValue();
        } else if (neededType.equals(Double.class) || neededType.equals(Double.TYPE)) {
            return numberValue.doubleValue();
        } else if (neededType.equals(Float.class) || neededType.equals(Float.TYPE)) {
            return numberValue.floatValue();
        } else if (neededType.equals(Byte.class) || neededType.equals(Byte.TYPE)) {
            return numberValue.byteValue();
        } else if (neededType.equals(Short.class) || neededType.equals(Short.TYPE)) {
            return numberValue.shortValue();
        } else {
            throw new IllegalArgumentException("Unsupported number type: " + neededType);
        }
    }

    public static JsonElement getElement(final JsonObject jsonObject, final String jsonElementName) {
        Assert.requireNonNull(jsonObject, "jsonObject");
        Assert.requireNonNull(jsonElementName, "jsonElementName");
        JsonElement element = jsonObject.get(jsonElementName);
        Assert.requireNonNull(element, "element");
        return element;
    }

    public static String getStringElement(final JsonObject jsonObject, final String jsonElementName) {
        return getElement(jsonObject, jsonElementName).getAsString();
    }

    public static Map<String, Serializable> getAsMap(final JsonObject jsonObject, final String jsonElementName) {
        final Map<String, Serializable> result = new HashMap<>();
        final JsonArray array = getElement(jsonObject, jsonElementName).getAsJsonArray();
        array.forEach(i -> {
            final JsonObject element = i.getAsJsonObject();
            final String key = getStringElement(element, NAME);
            final JsonPrimitive value = getElement(element, VALUE).getAsJsonPrimitive();
            final String typeName = getStringElement(element, TYPE);
            final JsonPrimitiveType type = JsonPrimitiveType.ofType(typeName);
            result.put(key, type.getValueOfElement(value));
        });
        return result;
    }

    public static boolean isElementJsonNull(final JsonObject jsonObject, final String jsonElementName) {
        return getElement(jsonObject, jsonElementName).isJsonNull();
    }

    public static JsonArray convertToJsonObject(final Map<String, Serializable> map) {
        Assert.requireNonNull(map, "map");
        final JsonArray array = new JsonArray();
        map.keySet().forEach(key -> {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(NAME, key);
            final Object value = map.get(key);
            if(value == null) {
                //For NULL we do not care about the type
                jsonObject.addProperty(TYPE, JsonPrimitiveType.STRING.getType());
                jsonObject.add(VALUE, JsonNull.INSTANCE);
            } else if(value instanceof BigDecimal) {
                jsonObject.addProperty(TYPE, JsonPrimitiveType.BIG_DECIMAL.getType());
                jsonObject.add(VALUE, new JsonPrimitive((BigDecimal) value));
            } else if(value instanceof BigInteger) {
                jsonObject.addProperty(TYPE, JsonPrimitiveType.BIG_INTEGER.getType());
                jsonObject.add(VALUE, new JsonPrimitive((BigInteger) value));
            } else if(value instanceof Boolean) {
                jsonObject.addProperty(TYPE, JsonPrimitiveType.BOOLEAN.getType());
                jsonObject.add(VALUE, new JsonPrimitive((Boolean) value));
            } else if(value instanceof Byte) {
                jsonObject.addProperty(TYPE, JsonPrimitiveType.BYTE.getType());
                jsonObject.add(VALUE, new JsonPrimitive((Byte) value));
            } else if(value instanceof Character) {
                jsonObject.addProperty(TYPE, JsonPrimitiveType.CHARACTER.getType());
                jsonObject.add(VALUE, new JsonPrimitive((Character) value));
            } else if(value instanceof Double) {
                jsonObject.addProperty(TYPE, JsonPrimitiveType.DOUBLE.getType());
                jsonObject.add(VALUE, new JsonPrimitive((Double) value));
            } else if(value instanceof Float) {
                jsonObject.addProperty(TYPE, JsonPrimitiveType.FLOAT.getType());
                jsonObject.add(VALUE, new JsonPrimitive((Float) value));
            } else if(value instanceof Integer) {
                jsonObject.addProperty(TYPE, JsonPrimitiveType.INT.getType());
                jsonObject.add(VALUE, new JsonPrimitive((Integer) value));
            } else if(value instanceof Long) {
                jsonObject.addProperty(TYPE, JsonPrimitiveType.LONG.getType());
                jsonObject.add(VALUE, new JsonPrimitive((Long) value));
            } else if(value instanceof Short) {
                jsonObject.addProperty(TYPE, JsonPrimitiveType.SHORT.getType());
                jsonObject.add(VALUE, new JsonPrimitive((Short) value));
            } else if(value instanceof String) {
                jsonObject.addProperty(TYPE, JsonPrimitiveType.STRING.getType());
                jsonObject.add(VALUE, new JsonPrimitive((String) value));
            } else {
                throw new IllegalArgumentException("Can not handle value of type '" + value.getClass() + "'");
            }
            array.add(jsonObject);
        });
        return array;
    }

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
