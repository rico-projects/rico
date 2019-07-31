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
package dev.rico.internal.server.remoting.legacy.communication;

import dev.rico.internal.remoting.legacy.communication.Command;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
@Deprecated
public class ActionRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(ActionRegistry.class);


    private final Map<Class<? extends Command>, List<CommandHandler>> actions = new HashMap();

    public Map<Class<? extends Command>, List<CommandHandler>> getActions() {
        return Collections.unmodifiableMap(actions);
    }

    public void register(final Class commandClass, final CommandHandler serverCommand) {
        Objects.requireNonNull(commandClass);
        Objects.requireNonNull(serverCommand);
        LOG.trace("Register handler for command type " + commandClass.getSimpleName());
        final List<CommandHandler> actions = getActionsFor(commandClass);
        if (!actions.contains(serverCommand)) {
            actions.add(serverCommand);
        }
    }

    public List<CommandHandler> getActionsFor(final Class<? extends Command> commandClass) {
        List<CommandHandler> actions = this.actions.get(commandClass);
        if (actions == null) {
            actions = new ArrayList<CommandHandler>();
            this.actions.put(commandClass, actions);
        }

        return actions;
    }
}
