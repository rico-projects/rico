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
package dev.rico.internal.server;


import dev.rico.internal.core.Assert;
import dev.rico.internal.server.bootstrap.PlatformBootstrap;
import dev.rico.internal.server.client.ClientSessionProvider;
import dev.rico.server.client.ClientSession;
import dev.rico.server.ClientScope;
import org.apiguardian.api.API;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Implementation of the {@link ClientScope} scope
 */
@API(since = "0.x", status = INTERNAL)
public class ClientScopeImpl implements Scope {

    public final static String CLIENT_SCOPE = "client";

    private final static String CLIENT_STORE_ATTRIBUTE = "SpringClientScopeStore";

    @Override
    public Object get(final String name, final ObjectFactory<?> objectFactory) {
        Assert.requireNonBlank(name, "name");
        Assert.requireNonNull(objectFactory, "objectFactory");
        Map<String, Object> localStore = getLocalStore();
        if (!localStore.containsKey(name)) {
            localStore.put(name, objectFactory.getObject());
        }
        return localStore.get(name);
    }

    @Override
    public Object remove(final String name) {
        return getLocalStore().remove(name);
    }

    @Override
    public void registerDestructionCallback(final String name, final Runnable callback) {

    }

    @Override
    public Object resolveContextualObject(final String key) {
        return null;
    }

    private Map<String, Object> getLocalStore() {
        ClientSession session = getClientSession();
        if(session == null) {
            throw new IllegalStateException("No client session found!");
        }
        Map<String, Object> localStore = session.getAttribute(CLIENT_STORE_ATTRIBUTE);
        if(localStore == null) {
            localStore = Collections.synchronizedMap(new HashMap<String, Object>());
            session.setAttribute(CLIENT_STORE_ATTRIBUTE, localStore);
        }
        return localStore;
    }

    @Override
    public String getConversationId() {
        return getClientSession().getId();
    }

    protected ClientSession getClientSession() {
        final ClientSessionProvider clientSessionProvider = PlatformBootstrap.getServerCoreComponents().getInstance(ClientSessionProvider.class);
        Assert.requireNonNull(clientSessionProvider, "clientSessionProvider");
        return clientSessionProvider.getCurrentClientSession();
    }
}
