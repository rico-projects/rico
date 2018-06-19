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
package dev.rico.internal.remoting.communication.codec;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.communication.codec.encoders.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import dev.rico.internal.remoting.communication.commands.Command;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public final class Codec {

    private static final Logger LOG = LoggerFactory.getLogger(Codec.class);

    private static final Codec INSTANCE = new Codec();

    private final Gson GSON;

    private final Map<Class<?>, CommandTranscoder<?>> transcoders = new HashMap<>();

    private Codec() {
        GSON = new GsonBuilder().serializeNulls().create();

        //TODO: add all commands
        addTranscoder(new CreateContextCommandTranscoder());
        addTranscoder(new CreateControllerCommandTranscoder());
        addTranscoder(new CallActionCommandTranscoder());
        addTranscoder(new DestroyControllerCommandTranscoder());
        addTranscoder(new DestroyContextCommandTranscoder());

    }

    private <C extends Command> void addTranscoder(final AbstractCommandTranscoder<C> transcoder) {
        Assert.requireNonNull(transcoder, "transcoder");
        final String id = transcoder.getId();
        if(transcoders.containsKey(id)) {
            throw new IllegalStateException("Transcoder for " + id + " already defined!");
        }
        transcoders.put(transcoder.getCommandClass(), transcoder);
    }

    public String encode(final List<? extends Command> commands) {
        Assert.requireNonNull(commands, "commands");
        LOG.debug("Encoding command list with {} commands", commands.size());
        final StringBuilder builder = new StringBuilder("[");
        for (final Command command : commands) {
            if (command == null) {
                throw new IllegalArgumentException("Command list contains a null command: " + command);
            } else {
                LOG.trace("Encoding command of type {}", command.getClass());
                final CommandTranscoder encoder = transcoders.get(command.getClass());
                if (encoder == null) {
                    throw new RuntimeException("No encoder for command type " + command.getClass() + " found");
                }
                final JsonObject jsonObject = encoder.encode(command);
                GSON.toJson(jsonObject, builder);
                builder.append(",");
            }
        }
        if (!commands.isEmpty()) {
            final int length = builder.length();
            builder.delete(length - 1, length);
        }
        builder.append("]");
        if (LOG.isTraceEnabled()) {
            LOG.trace("Encoded message: {}", builder.toString());
        }
        return builder.toString();
    }

    public List<Command> decode(final String transmitted) {
        Assert.requireNonNull(transmitted, "transmitted");
        LOG.trace("Decoding message: {}", transmitted);
        try {
            final JsonArray array = (JsonArray) new JsonParser().parse(transmitted);
            final List<Command> commands = new ArrayList<>(array.size());
            for (final JsonElement jsonElement : array) {
                final JsonObject command = (JsonObject) jsonElement;
                final JsonPrimitive idElement = command.getAsJsonPrimitive("id");
                if (idElement == null) {
                    throw new RuntimeException("Can not encode command without id!");
                }
                String id = idElement.getAsString();
                LOG.trace("Decoding command: {}", id);
                final CommandTranscoder<?> encoder = transcoders.get(id);
                if (encoder == null) {
                    throw new RuntimeException("Can not encode command of type " + id + ". No matching encoder found!");
                }
                final Command convertedCommand = encoder.decode(command);
                Assert.requireNonNull(convertedCommand, "convertedCommand");
                commands.add(convertedCommand);
            }
            LOG.debug("Decoded command list with {} commands", commands.size());
            return commands;
        } catch (Exception ex) {
            throw new JsonParseException("Illegal JSON detected", ex);
        }
    }

    public static Codec getInstance() {
        return INSTANCE;
    }
}
