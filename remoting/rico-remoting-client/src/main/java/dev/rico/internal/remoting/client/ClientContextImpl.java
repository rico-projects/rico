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
package dev.rico.internal.remoting.client;

import dev.rico.client.Client;
import dev.rico.client.concurrent.BackgroundExecutor;
import dev.rico.internal.remoting.client.legacy.ClientModelStore;
import dev.rico.internal.remoting.client.legacy.DefaultModelSynchronizer;
import dev.rico.internal.remoting.client.legacy.ModelSynchronizer;
import dev.rico.internal.remoting.client.legacy.communication.AbstractClientConnector;
import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.BeanManagerImpl;
import dev.rico.internal.remoting.BeanRepository;
import dev.rico.internal.remoting.BeanRepositoryImpl;
import dev.rico.internal.remoting.ClassRepository;
import dev.rico.internal.remoting.ClassRepositoryImpl;
import dev.rico.internal.remoting.Converters;
import dev.rico.internal.remoting.EventDispatcher;
import dev.rico.internal.remoting.PresentationModelBuilderFactory;
import dev.rico.internal.remoting.collections.ListMapperImpl;
import dev.rico.internal.remoting.commands.CreateContextCommand;
import dev.rico.internal.remoting.commands.DestroyContextCommand;
import dev.rico.client.ClientConfiguration;
import dev.rico.client.session.ClientSessionStore;
import dev.rico.remoting.BeanManager;
import dev.rico.remoting.RemotingException;
import dev.rico.remoting.client.ClientContext;
import dev.rico.remoting.client.ClientInitializationException;
import dev.rico.remoting.client.ControllerInitalizationException;
import dev.rico.remoting.client.ControllerProxy;
import org.apiguardian.api.API;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClientContextImpl implements ClientContext {

    private final ClientConfiguration clientConfiguration;

    private final Function<ClientModelStore, AbstractClientConnector> connectorProvider;

    private final URI endpoint;

    private final ClientSessionStore clientSessionStore;

    private final AbstractClientConnector clientConnector;

    private final ClientModelStore modelStore;

    @Deprecated
    private  final BeanManager clientBeanManager;

    private final ControllerProxyFactory controllerProxyFactory;

    private final RicoCommandHandler commandHandler;

    public ClientContextImpl(final ClientConfiguration clientConfiguration, final URI endpoint, final Function<ClientModelStore, AbstractClientConnector> connectorProvider, final ClientSessionStore clientSessionStore) {
        this.clientConfiguration = Assert.requireNonNull(clientConfiguration, "clientConfiguration");
        this.connectorProvider = Assert.requireNonNull(connectorProvider, "connectorProvider");
        this.clientSessionStore = Assert.requireNonNull(clientSessionStore, "clientSessionStore");
        this.endpoint = Assert.requireNonNull(endpoint, "endpoint");

        final ModelSynchronizer defaultModelSynchronizer = new DefaultModelSynchronizer(new Supplier<AbstractClientConnector>() {
            @Override
            public AbstractClientConnector get() {
                return clientConnector;
            }
        });

        this.modelStore = new ClientModelStore(defaultModelSynchronizer);
        this.clientConnector = connectorProvider.apply(modelStore);

        final EventDispatcher dispatcher = new ClientEventDispatcher(modelStore);
        final BeanRepository beanRepository = new BeanRepositoryImpl(modelStore, dispatcher);
        final Converters converters = new Converters(beanRepository);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(modelStore);
        final ClassRepository classRepository = new ClassRepositoryImpl(modelStore, converters, builderFactory);

        this.commandHandler = new RicoCommandHandler(clientConnector);
        this.controllerProxyFactory = new ControllerProxyFactory(commandHandler, clientConnector, modelStore, beanRepository, dispatcher, converters);
        this.clientBeanManager = new BeanManagerImpl(beanRepository, new ClientBeanBuilderImpl(classRepository, beanRepository, new ListMapperImpl(modelStore, classRepository, beanRepository, builderFactory, dispatcher), builderFactory, dispatcher));
    }

    protected RicoCommandHandler getCommandHandler() {
        return commandHandler;
    }

    @Override
    public <T> CompletableFuture<ControllerProxy<T>> createController(final String name, final Map<String, Serializable> parameters) {
        Assert.requireNonBlank(name, "name");

        if (controllerProxyFactory == null) {
            throw new IllegalStateException("connect was not called!");
        }

        return controllerProxyFactory.<T>create(name, parameters).handle((ControllerProxy<T> controllerProxy, Throwable throwable) -> {
            if (throwable != null) {
                throw new ControllerInitalizationException("Error while creating controller of type " + name, throwable);
            }
            return controllerProxy;
        });
    }

    @Override
    public synchronized BeanManager getBeanManager() {
        return clientBeanManager;
    }

    @Override
    public synchronized CompletableFuture<Void> disconnect() {
        final CompletableFuture<Void> result = new CompletableFuture<>();
        final BackgroundExecutor backgroundExecutor = Client.getService(BackgroundExecutor.class);
        backgroundExecutor.execute(() -> {
            commandHandler.invokeCommand(new DestroyContextCommand()).handle((Void aVoid, Throwable throwable) -> {

                clientConnector.disconnect();
                clientSessionStore.resetSession(endpoint);
                if (throwable != null) {
                    result.completeExceptionally(new RemotingException("Can't disconnect", throwable));
                } else {
                    result.complete(null);
                }
                return null;
            });
        });
        return result;
    }

    @Override
    public CompletableFuture<Void> connect() {

        final CompletableFuture<Void> result = new CompletableFuture<>();
        clientConnector.connect();
        final BackgroundExecutor backgroundExecutor = Client.getService(BackgroundExecutor.class);
        backgroundExecutor.execute(() -> {
            commandHandler.invokeCommand(new CreateContextCommand()).handle((Void aVoid, Throwable throwable) -> {
                if (throwable != null) {
                    result.completeExceptionally(new ClientInitializationException("Can't call init action!", throwable));
                } else {
                }
                result.complete(null);
                return null;
            });
        });
        return result;
    }

    @Override
    public String getClientId() {
        return clientSessionStore.getClientIdentifierForUrl(endpoint);
    }

}
