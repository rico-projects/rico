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
package dev.rico.internal.server.remoting.context;

import dev.rico.remoting.BeanManager;
import dev.rico.internal.core.Assert;
import dev.rico.internal.server.remoting.binding.PropertyBinderImpl;
import dev.rico.server.remoting.ClientSessionExecutor;
import dev.rico.server.remoting.RemotingContext;
import dev.rico.server.remoting.binding.PropertyBinder;
import dev.rico.server.remoting.event.RemotingEventBus;
import dev.rico.server.client.ClientSession;
import org.apiguardian.api.API;

import java.util.concurrent.Executor;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class RemotingContextImpl implements RemotingContext {

    private final ServerRemotingContext serverRemotingContext;

    private final RemotingEventBus eventBus;

    private final PropertyBinder propertyBinder = new PropertyBinderImpl();

    private final ClientSessionExecutor clientSessionExecutor;

    public RemotingContextImpl(final ServerRemotingContext serverRemotingContext, RemotingEventBus eventBus) {
        this.serverRemotingContext = Assert.requireNonNull(serverRemotingContext, "serverRemotingContext");
        this.eventBus = Assert.requireNonNull(eventBus, "eventBus");
        clientSessionExecutor = new ClientSessionExecutorImpl(new Executor() {
            @Override
            public void execute(Runnable command) {
                serverRemotingContext.runLater(command);
            }
        });
    }

    @Override
    public String getId() {
        return serverRemotingContext.getId();
    }

    @Override
    public ClientSessionExecutor createSessionExecutor() {
        return clientSessionExecutor;
    }

    @Override
    public PropertyBinder getBinder() {
        return propertyBinder;
    }

    @Override
    public BeanManager getBeanManager() {
        return serverRemotingContext.getBeanManager();
    }

    @Override
    public RemotingEventBus getEventBus() {
        return eventBus;
    }

    @Override
    public ClientSession getClientSession() {
        return serverRemotingContext.getClientSession();
    }

    @Override
    public boolean isActive() {
        return serverRemotingContext.isActive();
    }
}
