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
package dev.rico.internal.server.remoting;

import dev.rico.internal.core.Assert;
import dev.rico.internal.server.bootstrap.PlatformBootstrap;
import dev.rico.internal.server.remoting.context.ServerRemotingContext;
import dev.rico.internal.server.remoting.context.ServerRemotingContextProvider;
import dev.rico.internal.server.remoting.context.RemotingContextImpl;
import dev.rico.internal.server.remoting.event.LazyEventBusInvocationHandler;
import dev.rico.remoting.BeanManager;
import dev.rico.server.remoting.RemotingContext;
import dev.rico.server.remoting.binding.PropertyBinder;
import dev.rico.server.remoting.event.RemotingEventBus;
import dev.rico.server.ClientScoped;
import org.apiguardian.api.API;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.lang.reflect.Proxy;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Factory that provides all needed extensions as CDI beans.
 *
 * @author Hendrik Ebbers
 */
@ApplicationScoped
@API(since = "0.x", status = INTERNAL)
public class RemotingCdiBeanFactory {

    @Produces
    @ClientScoped
    public BeanManager createManager(RemotingContext remotingContext) {
        Assert.requireNonNull(remotingContext, "remotingContext");
        return remotingContext.getBeanManager();
    }

    @Produces
    @ClientScoped
    public RemotingContext createRemotingContext(RemotingEventBus eventBus) {
        Assert.requireNonNull(eventBus, "eventBus");

        final ServerRemotingContextProvider contextProvider = PlatformBootstrap.getServerCoreComponents().getInstance(ServerRemotingContextProvider.class);
        Assert.requireNonNull(contextProvider, "contextProvider");

        final ServerRemotingContext context =contextProvider.getCurrentContext();
        Assert.requireNonNull(context, "context");

        return new RemotingContextImpl(context, eventBus);
    }

    @Produces
    @ApplicationScoped
    public RemotingEventBus createEventBus() {
        return (RemotingEventBus) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{RemotingEventBus.class}, new LazyEventBusInvocationHandler());
    }

    @Produces
    @ClientScoped
    public PropertyBinder createPropertyBinder(RemotingContext remotingContext) {
        Assert.requireNonNull(remotingContext, "remotingContext");
        return remotingContext.getBinder();
    }
}
