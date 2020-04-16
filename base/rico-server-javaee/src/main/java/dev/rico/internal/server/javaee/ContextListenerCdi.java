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
package dev.rico.internal.server.javaee;

import dev.rico.internal.core.Assert;
import dev.rico.server.ServerListener;
import dev.rico.server.client.ClientSession;
import dev.rico.server.client.ClientSessionListener;
import dev.rico.server.javaee.ClientScoped;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
import org.apiguardian.api.API;

import javax.enterprise.inject.spi.BeanManager;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * This listener destroyes the {@link ClientScoped} CDI scope whenever a {@link ClientSession} will be destroyed
 */
@API(since = "0.x", status = INTERNAL)
@ServerListener
public class ContextListenerCdi implements ClientSessionListener {

    @Override
    public void sessionCreated(final ClientSession clientSession) {

    }

    @Override
    public void sessionDestroyed(final ClientSession clientSession) {
        Assert.requireNonNull(clientSession, "clientSession");
        final BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
        final ClientScopeContext clientContext = (ClientScopeContext) bm.getContext(ClientScoped.class);
        clientContext.destroy();
    }
}
