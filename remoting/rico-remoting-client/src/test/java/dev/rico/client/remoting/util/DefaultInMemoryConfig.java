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
package dev.rico.client.remoting.util;

import dev.rico.internal.client.remoting.legacy.ClientModelStore;
import dev.rico.internal.client.remoting.legacy.DefaultModelSynchronizer;
import dev.rico.internal.client.remoting.legacy.ModelSynchronizer;
import dev.rico.internal.client.remoting.legacy.communication.AbstractClientConnector;
import dev.rico.internal.client.remoting.legacy.communication.CommandBatcher;
import dev.rico.internal.server.remoting.legacy.ServerConnector;
import dev.rico.internal.server.remoting.legacy.ServerModelStore;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class DefaultInMemoryConfig implements Supplier<AbstractClientConnector> {

    private final ClientModelStore clientModelStore;

    private final ServerModelStore serverModelStore;

    private final InMemoryClientConnector clientConnector;

    private final ServerConnector serverConnector;


    public DefaultInMemoryConfig(final Executor uiExecutor) {

        serverModelStore = new ServerModelStore();
        serverConnector = new ServerConnector();
        serverConnector.setServerModelStore(serverModelStore);
        ModelSynchronizer defaultModelSynchronizer = new DefaultModelSynchronizer(this);
        clientModelStore = new ClientModelStore(defaultModelSynchronizer);
        clientConnector = new InMemoryClientConnector(clientModelStore, serverConnector, new CommandBatcher(), uiExecutor);
        clientConnector.setSleepMillis(100);
    }

    public ClientModelStore getClientModelStore() {
        return clientModelStore;
    }

    public ServerModelStore getServerModelStore() {
        return serverModelStore;
    }

    public InMemoryClientConnector getClientConnector() {
        return clientConnector;
    }

    public ServerConnector getServerConnector() {
        return serverConnector;
    }

    @Override
    public AbstractClientConnector get() {
        return clientConnector;
    }
}
