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
package dev.rico.internal.remoting.legacy.communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dev.rico.core.logging.Logger;
import dev.rico.core.logging.LoggerFactory;
import org.apiguardian.api.API;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apiguardian.api.API.Status.DEPRECATED;

@Deprecated
@API(since = "0.x", status = DEPRECATED)
public class JsonCodec implements Codec {

    private static final Logger LOG = LoggerFactory.getLogger(JsonCodec.class);

    public static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private final Gson GSON;

    public JsonCodec() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(final Date src, final Type typeOfSrc, final JsonSerializationContext context) {
                final JsonObject element = new JsonObject();
                element.addProperty(Date.class.toString(), new SimpleDateFormat(ISO8601_FORMAT).format(src));
                return element;
            }
        });
        gsonBuilder.registerTypeAdapter(Float.class, new JsonSerializer<Float>() {
            @Override
            public JsonElement serialize(final Float src, final Type typeOfSrc, final JsonSerializationContext context) {
                final JsonObject element = new JsonObject();
                element.addProperty(Float.class.toString(), Float.toString(src));
                return element;
            }
        });
        gsonBuilder.registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
            @Override
            public JsonElement serialize(final Double src, final Type typeOfSrc, final JsonSerializationContext context) {
                final JsonObject element = new JsonObject();
                element.addProperty(Double.class.toString(), Double.toString(src));
                return element;
            }
        });
        gsonBuilder.registerTypeAdapter(BigDecimal.class, new JsonSerializer<BigDecimal>() {
            @Override
            public JsonElement serialize(final BigDecimal src, final Type typeOfSrc, final JsonSerializationContext context) {
                final JsonObject element = new JsonObject();
                element.addProperty(BigDecimal.class.toString(), src.toString());
                return element;
            }
        });
        GSON = gsonBuilder.serializeNulls().create();
    }


    @Override
    public String encode(final List<? extends Command> commands) {
        final JsonArray ret = new JsonArray();

        for (final Command command : commands) {
            if (command == null) {
                throw new IllegalArgumentException("Command list contains a null command: " + command);
            } else {
                LOG.trace("Encoding command of type {}", command.getClass());
                final JsonElement element = GSON.toJsonTree(command);
                element.getAsJsonObject().addProperty("id", command.getId());
                element.getAsJsonObject().addProperty("className", command.getClass().getName());
                ret.add(element);
            }
        }
        return GSON.toJson(ret);
    }

    @Override
    public List<Command> decode(final String transmitted) {
        LOG.trace("Decoding message: {}", transmitted);
        try {
            final List<Command> commands = new ArrayList<>();
            final JsonArray array = (JsonArray) new JsonParser().parse(transmitted);

            for (final JsonElement jsonElement : array) {
                final JsonObject commandElement = (JsonObject) jsonElement;
                final String className = commandElement.getAsJsonPrimitive("className").getAsString();
                LOG.trace("Decoding command type: {}", className);
                Class<? extends Command> commandClass = (Class<? extends Command>) Class.forName(className);
                if (commandClass.equals(ValueChangedCommand.class)) {
                    commands.add(createValueChangedCommand(commandElement));
                } else if (commandClass.equals(CreatePresentationModelCommand.class)) {
                    commands.add(createCreatePresentationModelCommand(commandElement));
                } else {
                    commands.add(GSON.fromJson(commandElement, commandClass));
                }
            }
            LOG.trace("Decoded command list with {} commands", commands.size());
            return commands;
        } catch (Exception ex) {
            throw new JsonParseException("Illegal JSON detected", ex);
        }
    }

    private Command createCreatePresentationModelCommand(final JsonObject commandElement) {
        final CreatePresentationModelCommand command = new CreatePresentationModelCommand();
        command.setPmId(stringOrNull(commandElement.get("pmId")));
        command.setPmType(stringOrNull(commandElement.get("pmType")));
        command.setClientSideOnly(booleanOrFalse(commandElement.get("clientSideOnly")));

        if (commandElement.has("attributes")) {
            for (final JsonElement attributeElement : commandElement.getAsJsonArray("attributes")) {
                final JsonObject attributeObject = attributeElement.getAsJsonObject();
                final Map<String, Object> attributeMap = new HashMap<>();
                for (Map.Entry<String, JsonElement> entry : attributeObject.entrySet()) {
                    attributeMap.put(entry.getKey(), toValidValue(entry.getValue()));
                }
                command.getAttributes().add(attributeMap);
            }
        }

        return command;
    }

    private Command createValueChangedCommand(final JsonObject commandElement) {
        final ValueChangedCommand command = new ValueChangedCommand();
        command.setAttributeId(stringOrNull(commandElement.get("attributeId")));
        command.setNewValue(toValidValue(commandElement.get("newValue")));
        return command;
    }

    private boolean booleanOrFalse(final JsonElement element) {
        if (element.isJsonNull()) {
            return false;
        }
        return element.getAsBoolean();
    }

    private String stringOrNull(final JsonElement element) {
        if (element.isJsonNull()) {
            return null;
        }
        return element.getAsString();
    }

    private Object toValidValue(final JsonElement jsonElement) {
        if (jsonElement.isJsonNull()) {
            return null;
        } else if (jsonElement.isJsonPrimitive()) {
            final JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            } else if (primitive.isString()) {
                return primitive.getAsString();
            } else {
                return primitive.getAsNumber();
            }
        } else if (jsonElement.isJsonObject()) {
            final JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has(Date.class.toString())) {
                try {
                    return new SimpleDateFormat(ISO8601_FORMAT).parse(jsonObject.getAsJsonPrimitive(Date.class.toString()).getAsString());
                } catch (final Exception e) {
                    throw new RuntimeException("Can not converte!", e);
                }
            } else if (jsonObject.has(BigDecimal.class.toString())) {
                try {
                    return new BigDecimal(jsonObject.getAsJsonPrimitive(BigDecimal.class.toString()).getAsString());
                } catch (final Exception e) {
                    throw new RuntimeException("Can not converte!", e);
                }
            } else if (jsonObject.has(Float.class.toString())) {
                try {
                    return Float.valueOf(jsonObject.getAsJsonPrimitive(Float.class.toString()).getAsString());
                } catch (final Exception e) {
                    throw new RuntimeException("Can not converte!", e);
                }
            } else if (jsonObject.has(Double.class.toString())) {
                try {
                    return Double.valueOf(jsonObject.getAsJsonPrimitive(Double.class.toString()).getAsString());
                } catch (final Exception e) {
                    throw new RuntimeException("Can not converte!", e);
                }
            }
        }
        throw new RuntimeException("Can not converte!");
    }
}
