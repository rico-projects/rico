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
package dev.rico.internal.remoting.server.legacy;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.legacy.commands.InterruptLongPollCommand;
import dev.rico.internal.remoting.legacy.communication.Codec;
import dev.rico.internal.remoting.legacy.communication.Command;
import dev.rico.internal.remoting.server.legacy.action.CreatePresentationModelAction;
import dev.rico.internal.remoting.server.legacy.action.DeletePresentationModelAction;
import dev.rico.internal.remoting.server.legacy.action.AbstractServerAction;
import dev.rico.internal.remoting.server.legacy.action.StoreAttributeAction;
import dev.rico.internal.remoting.server.legacy.action.StoreValueChangeAction;
import dev.rico.internal.remoting.server.legacy.communication.ActionRegistry;
import dev.rico.internal.remoting.server.legacy.communication.CommandHandler;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ServerConnector {

    private static final Logger LOG = LoggerFactory.getLogger(ServerConnector.class);

    private final ActionRegistry registry = new ActionRegistry();

    private final List<AbstractServerAction> abstractServerActions = new ArrayList<AbstractServerAction>();

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    @Deprecated
    private Codec codec;

    private ServerModelStore serverModelStore;

    /**
     * doesn't fail on missing commands
     **/
    public List<Command> receive(final Command command) {

        Assert.requireNonNull(command, "command");
        LOG.trace("Received command of type {}", command.getClass().getSimpleName());
        List<Command> response = new LinkedList();// collecting parameter pattern

        if (!(command instanceof InterruptLongPollCommand)) {// signal commands must not update thread-confined state
            for (AbstractServerAction it : abstractServerActions) {
                it.setResponse(response);// todo: can be deleted as soon as all action refer to the SMS
            }

            serverModelStore.setCurrentResponse(response);
        }


        List<CommandHandler> actions = registry.getActionsFor(command.getClass());
        if (actions.isEmpty()) {
            LOG.warn("There is no server action registered for received command type {}, known commands types are {}", command.getClass().getSimpleName(), registry.getActions().keySet());
            return response;
        }

        // copying the list of actions allows an Action to unregister itself
        // avoiding ConcurrentModificationException to be thrown by the loop
        List<CommandHandler> actionsCopy = new ArrayList<CommandHandler>();
        actionsCopy.addAll(actions);
        try {
            for (CommandHandler action : actionsCopy) {
                action.handleCommand(command, response);
            }

        } catch (Exception exception) {
            throw exception;
        }

        return response;
    }

    public void register(final AbstractServerAction action) {
        action.setServerModelStore(serverModelStore);
        abstractServerActions.add(action);
        action.registerIn(registry);
    }

    public void registerDefaultActions() {
        if (initialized.getAndSet(true)) {
            LOG.warn("Attempt to initialize default actions more than once!");
            return;
        }
        register(new StoreValueChangeAction());
        register(new StoreAttributeAction());
        register(new CreatePresentationModelAction());
        register(new DeletePresentationModelAction());
    }

    @Deprecated
    public Codec getCodec() {
        return codec;
    }

    @Deprecated
    public void setCodec(final Codec codec) {
        this.codec = codec;
    }

    public void setServerModelStore(final ServerModelStore serverModelStore) {
        this.serverModelStore = serverModelStore;
    }

    public ActionRegistry getRegistry() {
        return registry;
    }

    @Deprecated
    public int getRegistrationCount() {
        return abstractServerActions.size();
    }

}
