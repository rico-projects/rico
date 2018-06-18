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
package dev.rico.internal.server.remoting.test;

import dev.rico.internal.client.remoting.legacy.ClientModelStore;
import dev.rico.internal.client.remoting.legacy.communication.AbstractClientConnector;
import dev.rico.internal.client.session.ClientSessionStoreImpl;
import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.legacy.communication.Command;
import dev.rico.internal.server.client.ClientSessionProvider;
import dev.rico.internal.server.client.HttpClientSessionImpl;
import dev.rico.internal.server.config.ConfigurationFileLoader;
import dev.rico.internal.server.config.ServerConfiguration;
import dev.rico.internal.server.remoting.config.RemotingConfiguration;
import dev.rico.internal.server.remoting.context.ServerRemotingContext;
import dev.rico.internal.server.remoting.controller.ControllerRepository;
import dev.rico.internal.server.scanner.DefaultClasspathScanner;
import dev.rico.client.Client;
import dev.rico.server.client.ClientSession;
import org.apiguardian.api.API;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.ROOT_PACKAGE_FOR_CLASSPATH_SCAN;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class TestConfiguration {

    private final ServerRemotingContext remotingTestContext;

    private final TestClientContext clientContext;

    public TestConfiguration(final WebApplicationContext context, final HttpSession httpSession) throws Exception {
        Assert.requireNonNull(context, "context");
        Assert.requireNonNull(httpSession, "httpSession");

        final ServerConfiguration defaultPlatformConfiguration = ConfigurationFileLoader.loadConfiguration();
        final RemotingConfiguration remotingConfiguration = new RemotingConfiguration(defaultPlatformConfiguration);

        //Client
        final ExecutorService clientExecutor = Executors.newSingleThreadExecutor();

        final ClientSessionStoreImpl clientSessionStore = new ClientSessionStoreImpl();
        final Function<ClientModelStore, AbstractClientConnector> connectorProvider = s -> new TestClientConnectorImpl(s, clientExecutor, c -> sendToServer(c));
        clientContext = new TestClientContextImpl(Client.getClientConfiguration(), new URI("http://dummy"), connectorProvider, clientSessionStore);

        //Server
        final ControllerRepository controllerRepository = new ControllerRepository(new DefaultClasspathScanner(defaultPlatformConfiguration.getListProperty(ROOT_PACKAGE_FOR_CLASSPATH_SCAN)));
        final TestSpringManagedBeanFactory containerManager = new TestSpringManagedBeanFactory(context);
        containerManager.init(context.getServletContext());
        final ClientSessionProviderMock clientSessionProviderMock = new ClientSessionProviderMock();


        remotingTestContext = new TestServerRemotingContext(remotingConfiguration, new HttpClientSessionImpl(httpSession), clientSessionProviderMock, containerManager, controllerRepository, createEmptyCallback());

        clientSessionProviderMock.setCurrentContext(remotingTestContext);
    }

    private Consumer<ServerRemotingContext> createEmptyCallback() {
        return (c) -> {
        };
    }

    private List<Command> sendToServer(final List<Command> commandList) {
        return remotingTestContext.handle(commandList);
    }

    public ServerRemotingContext getRemotingTestContext() {
        return remotingTestContext;
    }

    private class ClientSessionProviderMock implements ClientSessionProvider {

        private ServerRemotingContext currentContext;

        public void setCurrentContext(final ServerRemotingContext currentContext) {
            this.currentContext = currentContext;
        }

        @Override
        public ClientSession getCurrentClientSession() {
            return currentContext.getClientSession();
        }
    }

    public TestClientContext getClientContext() {
        return clientContext;
    }
}
