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
package dev.rico.server.remoting.context;

import dev.rico.internal.remoting.communication.commands.impl.CallActionCommand;
import dev.rico.internal.remoting.communication.commands.impl.CreateContextCommand;
import dev.rico.internal.remoting.communication.commands.impl.CreateControllerCommand;
import dev.rico.internal.remoting.communication.commands.impl.DestroyContextCommand;
import dev.rico.internal.remoting.communication.commands.impl.DestroyControllerCommand;
import dev.rico.internal.remoting.legacy.commands.InterruptLongPollCommand;
import dev.rico.internal.remoting.legacy.commands.StartLongPollCommand;
import dev.rico.internal.remoting.legacy.communication.Command;
import dev.rico.internal.server.beans.PostConstructInterceptor;
import dev.rico.internal.server.client.ClientSessionProvider;
import dev.rico.internal.server.client.HttpClientSessionImpl;
import dev.rico.internal.server.remoting.config.RemotingConfiguration;
import dev.rico.internal.server.remoting.context.ServerRemotingContext;
import dev.rico.internal.server.remoting.controller.ControllerRepository;
import dev.rico.internal.server.remoting.controller.ControllerValidationException;
import dev.rico.internal.server.remoting.legacy.communication.CommandHandler;
import dev.rico.internal.server.scanner.DefaultClasspathScanner;
import dev.rico.server.remoting.util.HttpSessionMock;
import dev.rico.server.client.ClientSession;
import dev.rico.server.spi.components.ManagedBeanFactory;
import org.testng.annotations.Test;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ServerRemotingContextTest {

    @Test
    public void testUniqueId() throws ControllerValidationException {
        //given:
        List<ServerRemotingContext> contextList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ServerRemotingContext serverRemotingContext = createContext();
            contextList.add(serverRemotingContext);
        }

        //then:
        assertEquals(contextList.size(), 1000);
        while (!contextList.isEmpty()) {
            ServerRemotingContext serverRemotingContext = contextList.remove(0);
            for (ServerRemotingContext toCompare : contextList) {
                assertFalse(serverRemotingContext.getId().equals(toCompare.getId()));
                assertTrue(serverRemotingContext.hashCode() != toCompare.hashCode());
                assertFalse(serverRemotingContext.equals(toCompare));
            }
        }
    }

    @Test
    public void testUniqueBeanManager() throws ControllerValidationException {
        //given:
        List<ServerRemotingContext> contextList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ServerRemotingContext serverRemotingContext = createContext();
            contextList.add(serverRemotingContext);
        }

        //then:
        while (!contextList.isEmpty()) {
            ServerRemotingContext serverRemotingContext = contextList.remove(0);
            for (ServerRemotingContext toCompare : contextList) {
                assertFalse(serverRemotingContext.getBeanManager().equals(toCompare.getBeanManager()));
            }
        }
    }

    @Test
    public void testUniqueContext() throws ControllerValidationException {
        //given:
        List<ServerRemotingContext> contextList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ServerRemotingContext serverRemotingContext = createContext();
            contextList.add(serverRemotingContext);
        }

        //then:
        while (!contextList.isEmpty()) {
            ServerRemotingContext serverRemotingContext = contextList.remove(0);
            for (ServerRemotingContext toCompare : contextList) {
                assertFalse(serverRemotingContext.getServerModelStore().equals(toCompare.getServerModelStore()));
            }
        }
    }

    @Test
    public void testUniqueClientSession() throws ControllerValidationException {
        //given:
        List<ServerRemotingContext> contextList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ServerRemotingContext serverRemotingContext = createContext();
            contextList.add(serverRemotingContext);
        }

        //then:
        while (!contextList.isEmpty()) {
            ServerRemotingContext serverRemotingContext = contextList.remove(0);
            for (ServerRemotingContext toCompare : contextList) {
                assertFalse(serverRemotingContext.getClientSession().equals(toCompare.getClientSession()));
            }
        }
    }

    @Test
    public void testGetterReturnValue() throws ControllerValidationException {
        //given:
        ServerRemotingContext serverRemotingContext = createContext();

        //then:
        assertNotNull(serverRemotingContext.getId());
        assertNotNull(serverRemotingContext.getBeanManager());
        assertNotNull(serverRemotingContext.getClientSession());
        assertNotNull(serverRemotingContext.getServerModelStore());
    }

    @Test
    public void testNewCommands() throws ControllerValidationException {
        //given:
        ServerRemotingContext serverRemotingContext = createContext();

        //then:
        Map<Class<? extends Command>, List<CommandHandler>> actions = serverRemotingContext.getServerConnector().getRegistry().getActions();
        assertNotNull(actions.containsKey(CreateContextCommand.class));
        assertNotNull(actions.containsKey(DestroyContextCommand.class));
        assertNotNull(actions.containsKey(CreateControllerCommand.class));
        assertNotNull(actions.containsKey(DestroyControllerCommand.class));
        assertNotNull(actions.containsKey(CallActionCommand.class));
        assertNotNull(actions.containsKey(StartLongPollCommand.class));
        assertNotNull(actions.containsKey(InterruptLongPollCommand.class));
    }

    private final DefaultClasspathScanner classpathScanner = new DefaultClasspathScanner("not.in.classpath");

    private ServerRemotingContext createContext() throws ControllerValidationException {
        final ClientSession session = new HttpClientSessionImpl(new HttpSessionMock());
        return new ServerRemotingContext(new RemotingConfiguration(), session, new ClientSessionProvider() {
            @Override
            public ClientSession getCurrentClientSession() {
                return session;
            }
        }, new ManagedBeanFactoryMock(), new ControllerRepository(classpathScanner), v -> {});
    }

    private class ManagedBeanFactoryMock implements ManagedBeanFactory {

        @Override
        public void init(ServletContext servletContext) {

        }

        @Override
        public <T> T createDependentInstance(Class<T> cls) {
            return null;
        }

        @Override
        public <T> T createDependentInstance(Class<T> cls, PostConstructInterceptor<T> interceptor) {
            return null;
        }

        @Override
        public <T> void destroyDependentInstance(T instance, Class<T> cls) {

        }


    }
}
