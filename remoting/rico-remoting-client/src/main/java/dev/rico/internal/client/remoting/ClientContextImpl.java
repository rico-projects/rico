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
package dev.rico.internal.client.remoting;

import dev.rico.client.Client;
import dev.rico.core.http.HttpClient;
import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.communication.codec.Codec;
import dev.rico.internal.remoting.communication.commands.Command;
import dev.rico.internal.remoting.communication.commands.impl.*;
import dev.rico.client.ClientConfiguration;
import dev.rico.client.session.ClientSessionStore;
import dev.rico.client.remoting.ClientContext;
import dev.rico.client.remoting.ControllerProxy;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClientContextImpl implements ClientContext {

    private static final Logger LOG = LoggerFactory.getLogger(ClientContextImpl.class);

    private final ClientConfiguration clientConfiguration;

    private final URI endpoint;

    private final ClientSessionStore clientSessionStore;

    private final ControllerProxyFactory controllerProxyFactory;

    private final HttpClientConnector clientConnector;

    private final ClientRepository clientRepository;

    public ClientContextImpl(final ClientConfiguration clientConfiguration, final URI endpoint, final ClientSessionStore clientSessionStore) {
        this.clientConfiguration = Assert.requireNonNull(clientConfiguration, "clientConfiguration");
        this.clientSessionStore = Assert.requireNonNull(clientSessionStore, "clientSessionStore");
        this.endpoint = Assert.requireNonNull(endpoint, "endpoint");
        final HttpClient httpClient = Client.getService(HttpClient.class);
        this.clientConnector = new HttpClientConnector(endpoint, clientConfiguration, Codec.getInstance(), httpClient, c -> handleResponseCommand(c), e -> handleError(e));
        this.clientRepository = new ClientRepository(c -> clientConnector.send(c));
        this.controllerProxyFactory = new ControllerProxyFactory(clientConnector, clientRepository);
    }

    private void handleResponseCommand(final Command command) {
        Assert.requireNonNull(command, "command");
        try {
            if (command instanceof CreateBeanTypeCommand) {
                clientRepository.onCreateBeanTypeCommand((CreateBeanTypeCommand) command);
            } else if (command instanceof CreateBeanCommand) {
                clientRepository.onCreateBeanCommand((CreateBeanCommand) command);
            } else if (command instanceof DeleteBeanCommand) {
                clientRepository.onBeanRemovedCommand((DeleteBeanCommand) command);
            } else if (command instanceof ValueChangedCommand) {
                clientRepository.onValueChangedCommand((ValueChangedCommand) command);
            } else if (command instanceof ListAddCommand) {
                clientRepository.onListAddCommand((ListAddCommand) command);
            } else if (command instanceof ListRemoveCommand) {
                clientRepository.onListRemoveCommand((ListRemoveCommand) command);
            } else if (command instanceof ListReplaceCommand) {
                clientRepository.onListReplaceCommand((ListReplaceCommand) command);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while handling response command " + command.getUniqueIdentifier(), e);
        }
    }

    private void handleError(final Exception exception) {
        Assert.requireNonNull(exception, "exception");
        LOG.error("Error in remoting!", exception);
        clientConnector.disconnect();
        clientRepository.clear();
        clientSessionStore.resetSession(endpoint);
    }

    @Override
    public <T> CompletableFuture<ControllerProxy<T>> createController(final String name) {
        Assert.requireNonBlank(name, "name");
        return controllerProxyFactory.<T>create(name);
    }

    @Override
    public CompletableFuture<Void> disconnect() {
        final DestroyContextCommand command = new DestroyContextCommand();
        return clientConnector.sendAndReact(command).whenComplete((v,e) -> {
            clientConnector.disconnect();
            clientRepository.clear();
            clientSessionStore.resetSession(endpoint);
        });
    }

    @Override
    public CompletableFuture<Void> connect() {
        final CreateContextCommand command = new CreateContextCommand();
        clientConnector.connect();
        return clientConnector.sendAndReact(command);
    }

    @Override
    public String getClientId() {
        return clientSessionStore.getClientIdentifierForUrl(endpoint);
    }

}
