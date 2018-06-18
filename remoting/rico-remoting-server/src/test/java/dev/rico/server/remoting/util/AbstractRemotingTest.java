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
package dev.rico.server.remoting.util;

import dev.rico.internal.remoting.*;
import dev.rico.internal.remoting.communication.converters.Converters;
import dev.rico.internal.remoting.legacy.communication.Command;
import dev.rico.internal.remoting.legacy.util.DirectExecutor;
import dev.rico.internal.remoting.repo.BeanRepository;
import dev.rico.internal.remoting.repo.ClassRepository;
import dev.rico.internal.server.remoting.config.RemotingConfiguration;
import dev.rico.internal.server.remoting.gc.GarbageCollectionCallback;
import dev.rico.internal.server.remoting.gc.GarbageCollector;
import dev.rico.internal.server.remoting.gc.Instance;
import dev.rico.internal.server.remoting.legacy.ServerModelStore;
import dev.rico.internal.server.remoting.model.ServerBeanBuilderImpl;
import dev.rico.remoting.BeanManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractRemotingTest {

    protected ServerModelStore createServerModelStore() {
        DefaultInMemoryConfig config = new DefaultInMemoryConfig(DirectExecutor.getInstance());
        config.getServerConnector().registerDefaultActions();

        ServerModelStore store = config.getServerModelStore();
        List<Command> commands = new ArrayList<>();
        store.setCurrentResponse(commands);

        return store;
    }

    protected EventDispatcher createEventDispatcher(ServerModelStore serverModelStore) {
        return new ServerEventDispatcher(serverModelStore);
    }

    protected BeanRepository createBeanRepository(ServerModelStore serverModelStore, EventDispatcher dispatcher) {
        return new BeanRepository(serverModelStore, dispatcher);
    }

    protected BeanManager createBeanManager(ServerModelStore serverModelStore, BeanRepository beanRepository, EventDispatcher dispatcher) {
        final Converters converters = new Converters(beanRepository);
        final PresentationModelBuilderFactory builderFactory = new ServerPresentationModelBuilderFactory(serverModelStore);
        final ClassRepository classRepository = new ClassRepository(serverModelStore, converters, builderFactory);
        final ListMapper listMapper = new ListMapperImpl(serverModelStore, classRepository, beanRepository, builderFactory, dispatcher);
        final RemotingConfiguration configurationForGc = new RemotingConfiguration();
        final GarbageCollector garbageCollector = new GarbageCollector(configurationForGc, new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {

            }
        });
        final AbstractBeanBuilder beanBuilder = new ServerBeanBuilderImpl(classRepository, beanRepository, listMapper, builderFactory, dispatcher, garbageCollector);
        return new BeanManagerImpl(beanRepository, beanBuilder);
    }


    protected BeanManager createBeanManager(ServerModelStore serverModelStore) {
        final EventDispatcher dispatcher = new ServerEventDispatcher(serverModelStore);
        final BeanRepository beanRepository = new BeanRepository(serverModelStore, dispatcher);
        final Converters converters = new Converters(beanRepository);
        final PresentationModelBuilderFactory builderFactory = new ServerPresentationModelBuilderFactory(serverModelStore);
        final ClassRepository classRepository = new ClassRepository(serverModelStore, converters, builderFactory);
        final ListMapper listMapper = new ListMapperImpl(serverModelStore, classRepository, beanRepository, builderFactory, dispatcher);
        final RemotingConfiguration configurationForGc = new RemotingConfiguration();
        final GarbageCollector garbageCollector = new GarbageCollector(configurationForGc, new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {

            }
        });
        final AbstractBeanBuilder beanBuilder = new ServerBeanBuilderImpl(classRepository, beanRepository, listMapper, builderFactory, dispatcher, garbageCollector);
        return new BeanManagerImpl(beanRepository, beanBuilder);
    }
}
