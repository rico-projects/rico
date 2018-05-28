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
package dev.rico.server.remoting.event;

import dev.rico.internal.server.beans.PostConstructInterceptor;
import dev.rico.internal.server.client.ClientSessionLifecycleHandlerImpl;
import dev.rico.internal.server.client.ClientSessionProvider;
import dev.rico.internal.server.client.HttpClientSessionImpl;
import dev.rico.internal.server.remoting.config.RemotingConfiguration;
import dev.rico.internal.server.remoting.event.DefaultRemotingEventBus;
import dev.rico.internal.server.remoting.context.ServerRemotingContext;
import dev.rico.internal.server.remoting.context.ServerRemotingContextProvider;
import dev.rico.internal.server.remoting.controller.ControllerRepository;
import dev.rico.internal.server.scanner.DefaultClasspathScanner;
import dev.rico.server.remoting.util.HttpSessionMock;
import dev.rico.core.functional.Subscription;
import dev.rico.server.client.ClientSession;
import dev.rico.server.spi.components.ManagedBeanFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.servlet.ServletContext;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultMessageEventImplBusTest {

    private final static Topic<String> TEST_TOPIC = Topic.create();

    @Test
    public void TestPublishOutsideSession() {
        RemotingEventBus eventBus = create(null);
        eventBus.publish(TEST_TOPIC, "huhu");
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void TestCanNotSubscribeOutsideSession() {
        RemotingEventBus eventBus = create(null);
        eventBus.subscribe(TEST_TOPIC, new MessageListener<String>() {
            @Override
            public void onMessage(MessageEvent<String> message) {
            }
        });
        Assert.fail();
    }


    @Test
    public void TestPublishInsideSession() {
        RemotingEventBus eventBus = create(createContext());
        eventBus.publish(TEST_TOPIC, "huhu");
    }

    @Test
    public void TestPublishInsideSessionCallsSubscriptionsDirectly() {
        //given
        final AtomicBoolean calledCheck = new AtomicBoolean(false);
        RemotingEventBus eventBus = create(createContext());
        eventBus.subscribe(TEST_TOPIC, new MessageListener<String>() {
            @Override
            public void onMessage(MessageEvent<String> message) {
                calledCheck.set(true);
            }
        });

        //when
        eventBus.publish(TEST_TOPIC, "huhu");

        //then
        Assert.assertTrue(calledCheck.get());
    }

    @Test
    public void TestRemoveSubscription() {
        //given
        final AtomicBoolean calledCheck = new AtomicBoolean(false);
        RemotingEventBus eventBus = create(createContext());
        Subscription subscription = eventBus.subscribe(TEST_TOPIC, new MessageListener<String>() {
            @Override
            public void onMessage(MessageEvent<String> message) {
                calledCheck.set(true);
            }
        });

        //when
        subscription.unsubscribe();
        eventBus.publish(TEST_TOPIC, "huhu");

        //then
        Assert.assertFalse(calledCheck.get());
    }

    private DefaultRemotingEventBus create(final ServerRemotingContext context) {
        DefaultRemotingEventBus eventBus = new DefaultRemotingEventBus();
        eventBus.init(new ServerRemotingContextProvider() {
            @Override
            public ServerRemotingContext getContext(ClientSession clientSession) {
                return getContextById(clientSession.getId());
            }

            @Override
            public ServerRemotingContext getContextById(String clientSessionId) {
                if (context != null && context.getId().equals(clientSessionId)) {
                    return context;
                }
                return null;
            }

            @Override
            public ServerRemotingContext getCurrentContext() {
                return context;
            }
        }, new ClientSessionLifecycleHandlerImpl());
        return eventBus;
    }

    private final DefaultClasspathScanner classpathScanner = new DefaultClasspathScanner("not.in.classpath");

    private ServerRemotingContext createContext() {
        try {
            final ClientSession session = new HttpClientSessionImpl(new HttpSessionMock());
            return new ServerRemotingContext(new RemotingConfiguration(), session, new ClientSessionProvider() {
                @Override
                public ClientSession getCurrentClientSession() {
                    return session;
                }
            }, new ManagedBeanFactoryMock(), new ControllerRepository(classpathScanner), v -> {});
        } catch (Exception e) {
            throw new RuntimeException("FAIL", e);
        }
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
