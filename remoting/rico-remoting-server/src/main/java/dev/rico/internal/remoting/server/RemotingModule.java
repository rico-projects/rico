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
package dev.rico.internal.remoting.server;

import dev.rico.internal.core.Assert;
import dev.rico.internal.core.lang.StreamUtils;
import dev.rico.internal.remoting.server.config.RemotingConfiguration;
import dev.rico.internal.remoting.server.context.DefaultRemotingContextFactory;
import dev.rico.internal.remoting.server.context.RemotingCommunicationHandler;
import dev.rico.internal.remoting.server.context.RemotingContextFactory;
import dev.rico.internal.remoting.server.context.ServerRemotingContext;
import dev.rico.internal.remoting.server.context.ServerRemotingContextProvider;
import dev.rico.internal.remoting.server.controller.ControllerValidationException;
import dev.rico.internal.remoting.server.event.AbstractEventBus;
import dev.rico.internal.remoting.server.servlet.InterruptServlet;
import dev.rico.internal.remoting.server.servlet.RemotingServlet;
import dev.rico.internal.server.bootstrap.AbstractBaseModule;
import dev.rico.internal.server.client.ClientSessionLifecycleHandler;
import dev.rico.internal.server.client.ClientSessionProvider;
import dev.rico.remoting.server.event.RemotingEventBus;
import dev.rico.remoting.server.event.spi.EventBusProvider;
import dev.rico.server.client.ClientSession;
import dev.rico.server.spi.ModuleDefinition;
import dev.rico.server.spi.ModuleInitializationException;
import dev.rico.server.spi.ServerCoreComponents;
import dev.rico.server.spi.components.ClasspathScanner;
import dev.rico.server.spi.components.ManagedBeanFactory;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.stream.Collectors;

import static dev.rico.internal.remoting.server.RemotingModule.REMOTING_MODULE;
import static dev.rico.internal.remoting.server.servlet.ServletConstants.INTERRUPT_SERVLET_NAME;
import static dev.rico.internal.remoting.server.servlet.ServletConstants.REMOTING_SERVLET_NAME;
import static dev.rico.internal.server.bootstrap.modules.ClientSessionModule.CLIENT_SESSION_MODULE;
import static org.apiguardian.api.API.Status.INTERNAL;

@ModuleDefinition(name = REMOTING_MODULE, moduleDependencies = CLIENT_SESSION_MODULE, order = 101)
@API(since = "0.x", status = INTERNAL)
public class RemotingModule extends AbstractBaseModule {

    private static final Logger LOG = LoggerFactory.getLogger(RemotingModule.class);

    public static final String REMOTING_MODULE = "RemotingModule";

    @Override
    protected String getActivePropertyName() {
        return RemotingConfiguration.ACTIVE;
    }

    @Override
    public void initialize(ServerCoreComponents coreComponents) throws ModuleInitializationException {
        LOG.info("Starting Rico remoting");

        final ServletContext servletContext = coreComponents.getServletContext();
        final ClasspathScanner classpathScanner = coreComponents.getClasspathScanner();
        final ManagedBeanFactory beanFactory = coreComponents.getManagedBeanFactory();
        final RemotingConfiguration configuration = new RemotingConfiguration(coreComponents.getConfiguration());
        final ClientSessionProvider sessionProvider = coreComponents.getInstance(ClientSessionProvider.class);
        final RemotingContextFactory remotingContextFactory = createRemotingContextFactory(classpathScanner, beanFactory, configuration, sessionProvider);
        final RemotingCommunicationHandler communicationHandler = new RemotingCommunicationHandler(sessionProvider, remotingContextFactory);
        final ServerRemotingContextProvider contextProvider = new DefaultRemotingContextProvider(communicationHandler);
        final ClientSessionLifecycleHandler lifecycleHandler = coreComponents.getInstance(ClientSessionLifecycleHandler.class);

        coreComponents.provideInstance(ServerRemotingContextProvider.class, contextProvider);

        servletContext.addServlet(REMOTING_SERVLET_NAME, new RemotingServlet(communicationHandler)).addMapping(configuration.getServletMapping());
        servletContext.addServlet(INTERRUPT_SERVLET_NAME, new InterruptServlet(contextProvider)).addMapping(configuration.getInterruptServletMapping());

        LOG.debug("Rico remoting initialized under context \"" + servletContext.getContextPath() + "\"");
        LOG.debug("Rico remoting endpoint defined as " + configuration.getServletMapping());

        final String eventbusType = configuration.getEventbusType();
        final List<EventBusProvider> providers = StreamUtils.loadServiceAsStream(EventBusProvider.class)
                .filter(provider -> eventbusType.equals(provider.getType()))
                .collect(Collectors.toList());

        checkSingleEventbusProvider(eventbusType, providers);

        final EventBusProvider provider = providers.get(0);
        LOG.debug("Using event bus of type {} with provider class {}", provider.getType(), provider.getClass());
        final RemotingEventBus eventBus = provider.create(configuration);
        if (eventBus instanceof AbstractEventBus) {
            ((AbstractEventBus) eventBus).init(contextProvider, lifecycleHandler);
        }

        coreComponents.provideInstance(RemotingEventBus.class, eventBus);
    }

    private DefaultRemotingContextFactory createRemotingContextFactory(
            final ClasspathScanner classpathScanner,
            final ManagedBeanFactory beanFactory,
            final RemotingConfiguration configuration,
            final ClientSessionProvider sessionProvider
    ) throws ModuleInitializationException {
        try {
            return new DefaultRemotingContextFactory(configuration, sessionProvider, beanFactory, classpathScanner);
        } catch (ControllerValidationException e) {
            throw new ModuleInitializationException("Can not start Remote Presentation Model support based on bad controller definition", e);
        }
    }

    private void checkSingleEventbusProvider(String eventbusType, List<EventBusProvider> providers) throws ModuleInitializationException {
        if (providers.isEmpty()) {
            throw new ModuleInitializationException("No event bus with type " + eventbusType + " found.");
        }
        if (providers.size() > 1) {
            final String duplicates = providers.stream()
                    .map(Object::getClass)
                    .map(Class::getName)
                    .collect(Collectors.joining());
            throw new ModuleInitializationException("Multiple event bus provider found " + duplicates);
        }
    }

    private static class DefaultRemotingContextProvider implements ServerRemotingContextProvider {
        private final RemotingCommunicationHandler communicationHandler;

        public DefaultRemotingContextProvider(RemotingCommunicationHandler communicationHandler) {
            this.communicationHandler = Assert.requireNonNull(communicationHandler, "communicationHandler");
        }

        @Override
        public ServerRemotingContext getContext(final ClientSession clientSession) {
            return communicationHandler.getContext(clientSession);
        }

        @Override
        public ServerRemotingContext getContextById(String clientSessionId) {
            return communicationHandler.getContextById(clientSessionId);
        }

        @Override
        public ServerRemotingContext getCurrentContext() {
            return communicationHandler.getCurrentRemotingContext();
        }
    }
}
