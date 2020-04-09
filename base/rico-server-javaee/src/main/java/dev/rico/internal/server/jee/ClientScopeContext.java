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
package dev.rico.internal.server.jee;

import dev.rico.internal.core.Assert;
import dev.rico.internal.server.bootstrap.PlatformBootstrap;
import dev.rico.internal.server.client.ClientSessionProvider;
import dev.rico.server.client.ClientSession;
import dev.rico.server.jee.ClientScoped;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;
import org.apiguardian.api.API;

import javax.enterprise.context.ContextException;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClientScopeContext extends AbstractContext {

    private final static String CLIENT_STORAGE_ATTRIBUTE = "ClientSessionCdiContextualStorage";

    private final BeanManager beanManager;

    public ClientScopeContext(final BeanManager beanManager) {
        super(beanManager);
        this.beanManager = beanManager;
    }

    @Override
    protected ContextualStorage getContextualStorage(final Contextual<?> contextual, final boolean createIfNotExist) {
        final Object val = getClientSession().getAttribute(CLIENT_STORAGE_ATTRIBUTE);
        if(val != null) {
            if(val instanceof ContextualStorage) {
                return (ContextualStorage) val;
            } else {
                throw new ContextException("No ClientContext specified!");
            }
        } else {
            if(createIfNotExist) {
                final ContextualStorage contextualStorage = new ContextualStorage(beanManager, false, false);
                getClientSession().setAttribute(CLIENT_STORAGE_ATTRIBUTE, contextualStorage);
                return contextualStorage;
            } else {
                return null;
            }
        }
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ClientScoped.class;
    }

    public boolean isActive() {
        return getClientSession() != null;
    }

    private ClientSession getClientSession() {
        final ClientSessionProvider provider = PlatformBootstrap.getServerCoreComponents().getInstance(ClientSessionProvider.class);
        Assert.requireNonNull(provider, "provider");
        return provider.getCurrentClientSession();
    }

    public void destroy() {
        final Object val = getClientSession().getAttribute(CLIENT_STORAGE_ATTRIBUTE);
        if(val != null && val instanceof ContextualStorage) {
            AbstractContext.destroyAllActive((ContextualStorage) val);
        }
    }
}
