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
import dev.rico.internal.core.context.ContextManagerImpl;
import dev.rico.internal.server.bootstrap.PlatformBootstrap;
import dev.rico.internal.server.client.ClientSessionProvider;
import dev.rico.internal.server.remoting.servlet.ServerTimingFilter;
import dev.rico.core.context.ContextManager;
import dev.rico.server.client.ClientSession;
import dev.rico.server.ClientScoped;
import dev.rico.server.timing.ServerTiming;
import org.apiguardian.api.API;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Factory that provides all needed extensions as CDI beans.
 *
 * @author Hendrik Ebbers
 */
@ApplicationScoped
@API(since = "0.x", status = INTERNAL)
public class CdiBeanFactory {

    @Produces
    @ClientScoped
    public ClientSession createClientSession() {
        final ClientSessionProvider provider = PlatformBootstrap.getServerCoreComponents().getInstance(ClientSessionProvider.class);
        Assert.requireNonNull(provider, "provider");
        return provider.getCurrentClientSession();
    }

    @Produces
    @RequestScoped
    public ServerTiming createServerTiming() {
        return ServerTimingFilter.getCurrentTiming();
    }

    @Produces
    @ApplicationScoped
    public ContextManager createContextManager() {
        return ContextManagerImpl.getInstance();
    }
}
