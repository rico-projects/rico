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
package dev.rico.client.remoting.util;

import dev.rico.internal.remoting.client.ClientBeanBuilderImpl;
import dev.rico.internal.remoting.client.ClientEventDispatcher;
import dev.rico.internal.remoting.client.ClientPresentationModelBuilderFactory;
import dev.rico.internal.remoting.client.legacy.ClientModelStore;
import dev.rico.internal.remoting.client.legacy.DefaultModelSynchronizer;
import dev.rico.internal.remoting.client.legacy.ModelSynchronizer;
import dev.rico.internal.remoting.client.legacy.communication.AbstractClientConnector;
import dev.rico.internal.remoting.BeanBuilder;
import dev.rico.internal.remoting.BeanManagerImpl;
import dev.rico.internal.remoting.BeanRepository;
import dev.rico.internal.remoting.BeanRepositoryImpl;
import dev.rico.internal.remoting.ClassRepository;
import dev.rico.internal.remoting.ClassRepositoryImpl;
import dev.rico.internal.remoting.Converters;
import dev.rico.internal.remoting.EventDispatcher;
import dev.rico.internal.remoting.ListMapper;
import dev.rico.internal.remoting.PresentationModelBuilderFactory;
import dev.rico.internal.remoting.collections.ListMapperImpl;
import dev.rico.internal.remoting.legacy.communication.Command;
import dev.rico.internal.remoting.legacy.util.DirectExecutor;
import dev.rico.internal.remoting.server.legacy.ServerConnector;
import dev.rico.internal.remoting.server.legacy.ServerModelStore;
import dev.rico.remoting.BeanManager;

import java.util.ArrayList;

public abstract class AbstractRemotingTest {

    public class TestConfiguration {

        private final ClientModelStore clientModelStore;

        private final ServerModelStore serverModelStore;

        private final InMemoryClientConnector clientConnector;

        private final ServerConnector serverConnector;


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

        public TestConfiguration(ClientModelStore clientModelStore, ServerModelStore serverModelStore, InMemoryClientConnector clientConnector, ServerConnector serverConnector) {
            this.clientModelStore = clientModelStore;
            this.serverModelStore = serverModelStore;
            this.clientConnector = clientConnector;
            this.serverConnector = serverConnector;
        }
    }

    protected ClientModelStore createClientModelStore(final AbstractClientConnector connector) {
        ModelSynchronizer defaultModelSynchronizer = new DefaultModelSynchronizer(() -> connector);
        ClientModelStore clientModelStore = new ClientModelStore(defaultModelSynchronizer);

        return clientModelStore;
    }

    protected TestConfiguration createTestConfiguration() {
        DefaultInMemoryConfig config = new DefaultInMemoryConfig(DirectExecutor.getInstance());
        config.getServerConnector().registerDefaultActions();
        ServerModelStore store = config.getServerModelStore();
        store.setCurrentResponse(new ArrayList<>());

        return new TestConfiguration(config.getClientModelStore(), config.getServerModelStore(), config.getClientConnector(), config.getServerConnector());
    }

    protected EventDispatcher createEventDispatcher(final ClientModelStore clientModelStore) {
        final EventDispatcher dispatcher = new ClientEventDispatcher(clientModelStore);
        return dispatcher;
    }

    protected BeanRepository createBeanRepository(final ClientModelStore clientModelStore, final EventDispatcher dispatcher) {
        final BeanRepositoryImpl beanRepository = new BeanRepositoryImpl(clientModelStore, dispatcher);
        return beanRepository;
    }

    protected BeanManager createBeanManager(final ClientModelStore clientModelStore, final BeanRepository beanRepository, final EventDispatcher dispatcher) {
        final Converters converters = new Converters(beanRepository);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(clientModelStore);
        final ClassRepository classRepository = new ClassRepositoryImpl(clientModelStore, converters, builderFactory);
        final ListMapper listMapper = new ListMapperImpl(clientModelStore, classRepository, beanRepository, builderFactory, dispatcher);
        final BeanBuilder beanBuilder = new ClientBeanBuilderImpl(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
        return new BeanManagerImpl(beanRepository, beanBuilder);
    }

    protected BeanManager createBeanManager(final ClientModelStore clientModelStore) {
        final EventDispatcher dispatcher = createEventDispatcher(clientModelStore);
        final BeanRepository repository = createBeanRepository(clientModelStore, dispatcher);
        return createBeanManager(clientModelStore, repository, dispatcher);
    }
}
