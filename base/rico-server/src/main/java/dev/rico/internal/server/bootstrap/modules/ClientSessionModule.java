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
package dev.rico.internal.server.bootstrap.modules;

import dev.rico.internal.core.Assert;
import dev.rico.internal.server.client.ClientSessionFilter;
import dev.rico.internal.server.client.ClientSessionLifecycleHandler;
import dev.rico.internal.server.client.ClientSessionLifecycleHandlerImpl;
import dev.rico.internal.server.client.ClientSessionManager;
import dev.rico.internal.server.client.ClientSessionMutextHolder;
import dev.rico.internal.server.client.ClientSessionProvider;
import dev.rico.internal.server.client.HttpSessionCleanerListener;
import dev.rico.core.Configuration;
import dev.rico.server.ServerListener;
import dev.rico.server.client.ClientSession;
import dev.rico.server.client.ClientSessionListener;
import dev.rico.internal.server.bootstrap.AbstractBaseModule;
import dev.rico.server.spi.ModuleDefinition;
import dev.rico.server.spi.ModuleInitializationException;
import dev.rico.server.spi.ServerCoreComponents;
import dev.rico.server.spi.components.ClasspathScanner;
import dev.rico.server.spi.components.ManagedBeanFactory;
import org.apiguardian.api.API;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.ID_FILTER_URL_MAPPINGS;
import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.ID_FILTER_URL_MAPPINGS_DEFAULT_VALUE;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
@ModuleDefinition
public class ClientSessionModule extends AbstractBaseModule {

    public static final String CLIENT_SESSION_MODULE = "ClientSessionModule";

    public static final String CLIENT_SESSION_MODULE_ACTIVE = "clientSessionActive";

    public static final String CLIENT_ID_FILTER_NAME = "clientIdFilter";

    @Override
    protected String getActivePropertyName() {
        return CLIENT_SESSION_MODULE_ACTIVE;
    }

    @Override
    public String getName() {
        return CLIENT_SESSION_MODULE;
    }

    @Override
    public void initialize(final ServerCoreComponents coreComponents) throws ModuleInitializationException {
        Assert.requireNonNull(coreComponents, "coreComponents");

        final ServletContext servletContext = coreComponents.getInstance(ServletContext.class);
        final Configuration configuration = coreComponents.getConfiguration();
        final ClasspathScanner classpathScanner = coreComponents.getInstance(ClasspathScanner.class);
        final ManagedBeanFactory beanFactory = coreComponents.getInstance(ManagedBeanFactory.class);

        final ClientSessionLifecycleHandlerImpl lifecycleHandler = new ClientSessionLifecycleHandlerImpl();
        coreComponents.provideInstance(ClientSessionLifecycleHandler.class, lifecycleHandler);
                coreComponents.provideInstance(ClientSessionProvider.class, new ClientSessionProvider() {
            @Override
            public ClientSession getCurrentClientSession() {
                return lifecycleHandler.getCurrentClientSession();
            }
        });


        final ClientSessionManager clientSessionManager = new ClientSessionManager(configuration, lifecycleHandler);

        final List<String> endpointList = configuration.getListProperty(ID_FILTER_URL_MAPPINGS, ID_FILTER_URL_MAPPINGS_DEFAULT_VALUE);
        final String[] endpoints = endpointList.toArray(new String[endpointList.size()]);
        final ClientSessionFilter filter = new ClientSessionFilter(clientSessionManager);
        final FilterRegistration.Dynamic createdFilter = servletContext.addFilter(CLIENT_ID_FILTER_NAME, filter);
        createdFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, endpoints);

        final HttpSessionCleanerListener sessionCleaner = new HttpSessionCleanerListener(clientSessionManager);
        servletContext.addListener(sessionCleaner);

        final Set<Class<?>> listeners = classpathScanner.getTypesAnnotatedWith(ServerListener.class);
        for (final Class<?> listenerClass : listeners) {
            if (ClientSessionListener.class.isAssignableFrom(listenerClass)) {
                final ClientSessionListener listener = (ClientSessionListener) beanFactory.createDependentInstance(listenerClass);

                lifecycleHandler.addSessionDestroyedListener(s -> listener.sessionDestroyed(s));
                lifecycleHandler.addSessionCreatedListener(s -> listener.sessionCreated(s));
            }
        }

        final ClientSessionMutextHolder mutextHolder = new ClientSessionMutextHolder();
        lifecycleHandler.addSessionDestroyedListener(s -> mutextHolder.sessionDestroyed(s));
        lifecycleHandler.addSessionCreatedListener(s -> mutextHolder.sessionCreated(s));

    }
}
