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
package dev.rico.internal.remoting.server.spring;

import dev.rico.internal.remoting.BeanManagerImpl;
import dev.rico.internal.remoting.server.context.ServerRemotingContext;
import dev.rico.remoting.BeanManager;
import dev.rico.internal.core.Assert;
import dev.rico.internal.server.bootstrap.PlatformBootstrap;
import dev.rico.internal.remoting.server.context.ServerRemotingContextProvider;
import dev.rico.internal.remoting.server.context.RemotingContextImpl;
import dev.rico.internal.remoting.server.event.LazyEventBusInvocationHandler;
import dev.rico.remoting.server.RemotingContext;
import dev.rico.remoting.server.binding.PropertyBinder;
import dev.rico.remoting.server.event.RemotingEventBus;
import dev.rico.server.spring.ClientScope;
import dev.rico.server.spring.SingletonScope;
import org.apiguardian.api.API;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Provides all Beans and Scopes for CDI
 */
@Configuration
@API(since = "0.x", status = INTERNAL)
public class RemotingSpringBeanFactory {

    @Bean(name = "remotingContext")
    @ClientScope
    protected RemotingContext createRemotingContext(RemotingEventBus eventBus) {
        Assert.requireNonNull(eventBus, "eventBus");

        final ServerRemotingContextProvider contextProvider = PlatformBootstrap.getServerCoreComponents().getInstance(ServerRemotingContextProvider.class);
        Assert.requireNonNull(contextProvider, "contextProvider");

        final ServerRemotingContext context = contextProvider.getCurrentContext();
        Assert.requireNonNull(context, "context");

        return new RemotingContextImpl(context, eventBus);
    }

    /**
     * Method to create a spring managed {@link BeanManagerImpl} instance in client scope.
     *
     * @return the instance
     */
    @Bean(name = "beanManager")
    @ClientScope
    protected BeanManager createManager(RemotingContext remotingContext) {
        Assert.requireNonNull(remotingContext, "remotingContext");
        return remotingContext.getBeanManager();
    }

    /**
     * Method to create a spring managed {@link RemotingEventBus} instance in singleton scope.
     *
     * @return the instance
     */
    @Bean(name = "remotingEventBus")
    @SingletonScope
    protected RemotingEventBus createEventBus() {
        return (RemotingEventBus) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{RemotingEventBus.class}, new LazyEventBusInvocationHandler());
    }

    @Bean(name = "propertyBinder")
    @ClientScope
    protected PropertyBinder createPropertyBinder(RemotingContext remotingContext) {
        Assert.requireNonNull(remotingContext, "remotingContext");
        return remotingContext.getBinder();
    }
}
