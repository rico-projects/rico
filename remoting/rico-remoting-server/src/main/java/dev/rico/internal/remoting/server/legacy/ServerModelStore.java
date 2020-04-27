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
import dev.rico.internal.remoting.legacy.communication.Command;
import dev.rico.internal.remoting.legacy.communication.CreatePresentationModelCommand;
import dev.rico.internal.remoting.legacy.communication.DeletePresentationModelCommand;
import dev.rico.internal.remoting.legacy.communication.ValueChangedCommand;
import dev.rico.internal.remoting.legacy.core.ModelStore;
import dev.rico.internal.remoting.legacy.core.ModelStoreConfig;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * The ServerModelStore is a {@link ModelStore} with customized behavior appropriate to the
 * server side of a Dolphin connection.  There is one ServerModelSore for each user session.
 * The ServerModelStore self-assigns a unique ID which identifies each user session.
 */
@API(since = "0.x", status = INTERNAL)
public class ServerModelStore extends ModelStore<ServerAttribute, ServerPresentationModel> {

    private static final Logger LOG = LoggerFactory.getLogger(ServerModelStore.class);


    /**
     * thread safe unique store count across all sessions in order to create unique store ids.
     */
    private static final AtomicInteger storeCount = new AtomicInteger(0);

    /**
     * unique identification of the current user session.
     */
    public final int id = storeCount.getAndIncrement();

    protected List<Command> currentResponse = null;

    /**
     * Used to create unique presentation model ids within one server model store.
     */
    protected long pmInstanceCount = 0L;

    public ServerModelStore() {
    }

    public ServerModelStore(final ModelStoreConfig config) {
        super(config);
    }

    /**
     * A shared mutable state that is safe to use since we are thread-confined
     */
    protected List<Command> getCurrentResponse() {
        return currentResponse;
    }

    public boolean hasResponseCommands() {
        if(currentResponse != null && !currentResponse.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * A shared mutable state that is safe to use since we are thread-confined
     */
    public void setCurrentResponse(final List<Command> currentResponse) {
        this.currentResponse = currentResponse;
    }

    @Override
    public boolean add(final ServerPresentationModel model) {
        boolean added = super.add(model);
        if (!added) {
            throw new IllegalStateException("Model " + model + " already defined on server!");
        }
        model.setModelStore(this);
        getCurrentResponse().add(CreatePresentationModelCommand.makeFrom(model));
        return true;
    }

    public boolean checkClientAdded(final ServerPresentationModel model) {
        boolean added = super.add(model);
        //FIXME: Currently the client has the same event for a add answer and a add trigger

//        if (!added) {
  //          throw new IllegalStateException("Model " + model + " already defined on server!");
    //    }
        model.setModelStore(this);
        return true;
    }

    public int getId() {
        return id;
    }

    /**
     * Convenience method to let Dolphin removePresentationModel a presentation model directly on the server and notify the client.
     */
    public boolean remove(final ServerPresentationModel pm) {
        boolean deleted = super.remove(pm);
        if (!deleted) {
            throw new IllegalStateException("Model " + pm + " not found on the server!");
        }
        deleteCommand(getCurrentResponse(), pm.getId());
        return deleted;
    }

    public boolean checkClientRemoved(final ServerPresentationModel pm) {
        boolean deleted = super.remove(pm);

        //FIXME: Currently the client has the same event for a remove answer and a remove trigger
      //  if (!deleted) {
      //      throw new IllegalStateException("Model " + pm + " not found on the server!");
      //  }
        return deleted;
    }

    /**
     * Convenience method to let Dolphin delete a presentation model on the client side
     */
    @Deprecated
    public static void deleteCommand(final List<Command> response, final String pmId) {
        if (response == null || Assert.isBlank(pmId)) {
            return;
        }
        response.add(new DeletePresentationModelCommand(pmId));
    }

    /**
     * Convenience method to change an attribute value on the server side.
     *
     * @param response  must not be null or the method silently ignores the call
     * @param attribute must not be null
     */
    @Deprecated
    public static void changeValueCommand(final List<Command> response, final ServerAttribute attribute, final Object value) {
        if (response == null) {
            return;
        }
        if (attribute == null) {
            LOG.error("Cannot change value on a null attribute to '{}'", value);
            return;
        }
        forceChangeValue(value, response, attribute);
    }

    /**
     * @deprecated use forceChangeValueCommand(Object, List, ServerAttribute). You can use the "inline method refactoring". Will be removed in version 1.0!
     */
    @Deprecated
    public static void forceChangeValue(final Object value, final List<Command> response, final ServerAttribute attribute) {
        response.add(new ValueChangedCommand(attribute.getId(), value));
    }


    /**
     * Convenience method to let the client (!) dolphin create a presentation model as specified by the DTO.
     * The server model store remains untouched until the client has issued the notification.
     */
    @Deprecated
    public static void presentationModelCommand(final List<Command> response, final String id, final String presentationModelType, final DTO dto) {
        if (response == null) {
            return;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Slot slot : dto.getSlots()) {
            Map<String, Object> map = new HashMap<>();
            map.put("propertyName", slot.getPropertyName());
            map.put("value", slot.getValue());
            map.put("qualifier", slot.getQualifier());
            list.add(map);
        }
        response.add(new CreatePresentationModelCommand(id, presentationModelType, list));
    }

    /**
     * Create a presentation model on the server side, add it to the model store, and withContent a command to
     * the client, advising him to do the same.
     *
     * @throws IllegalArgumentException if a presentation model for this id already exists. No commands are sent in this case.
     */
    public ServerPresentationModel presentationModel(final String id, final String presentationModelType, final DTO dto) {
        List<ServerAttribute> attributes = new ArrayList<>();
        for (final Slot slot : dto.getSlots()) {
            final ServerAttribute result = new ServerAttribute(slot.getPropertyName(), slot.getValue(), slot.getQualifier());
            result.silently(new Runnable() {
                @Override
                public void run() {
                    result.setValue(slot.getValue());
                }

            });
            attributes.add(result);
        }
        ServerPresentationModel model = new ServerPresentationModel(id, attributes, this);
        model.setPresentationModelType(presentationModelType);
        add(model);
        return model;
    }
}
