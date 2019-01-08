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
package dev.rico.internal.server.remoting.test;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.BeanManagerImpl;
import dev.rico.internal.server.remoting.binding.PropertyBinderImpl;
import dev.rico.internal.server.client.ClientSessionLifecycleHandlerImpl;
import dev.rico.internal.server.remoting.context.ClientSessionExecutorImpl;
import dev.rico.internal.server.remoting.context.ServerRemotingContext;
import dev.rico.internal.server.remoting.context.ServerRemotingContextProvider;
import dev.rico.internal.server.remoting.event.DefaultRemotingEventBus;
import dev.rico.internal.server.ClientScopeImpl;
import dev.rico.remoting.BeanManager;
import dev.rico.server.remoting.ClientSessionExecutor;
import dev.rico.server.remoting.RemotingContext;
import dev.rico.server.remoting.binding.PropertyBinder;
import dev.rico.server.remoting.event.RemotingEventBus;
import dev.rico.server.client.ClientSession;
import org.apiguardian.api.API;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;
import java.util.concurrent.Executor;

import static org.apiguardian.api.API.Status.INTERNAL;

@Configuration
@API(since = "0.x", status = INTERNAL)
public class SpringTestBootstrap {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected TestConfiguration createTestConfiguration(final WebApplicationContext context, final HttpSession httpSession) {
        Assert.requireNonNull(context, "context");
        try {
            return new TestConfiguration(context, httpSession);
        } catch (Exception e) {
            throw new RuntimeException("Can not create test configuration", e);
        }
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected TestClientContext createClientContext(final TestConfiguration testConfiguration) {
        Assert.requireNonNull(testConfiguration, "testConfiguration");
        return testConfiguration.getClientContext();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected ServerRemotingContext createServerContext(final TestConfiguration testConfiguration) {
        Assert.requireNonNull(testConfiguration, "testConfiguration");
        return testConfiguration.getRemotingTestContext();
    }

    /**
     * Method to create a spring managed {@link BeanManagerImpl} instance in client scope.
     *
     * @return the instance
     */
    @Bean(name = "beanManager")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected BeanManager createManager(final TestConfiguration testConfiguration) {
        Assert.requireNonNull(testConfiguration, "testConfiguration");
        return testConfiguration.getRemotingTestContext().getBeanManager();
    }

    @Bean(name = "clientSession")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected ClientSession createClientSession(final TestConfiguration testConfiguration) {
        Assert.requireNonNull(testConfiguration, "testConfiguration");
        return testConfiguration.getRemotingTestContext().getClientSession();
    }

    @Bean(name = "remotingContext")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected RemotingContext createRemotingContext(final TestConfiguration testConfiguration, final PropertyBinder propertyBinder, final RemotingEventBus eventBus) {
        Assert.requireNonNull(testConfiguration, "testConfiguration");
        Assert.requireNonNull(propertyBinder, "propertyBinder");
        Assert.requireNonNull(eventBus, "eventBus");
        return new RemotingContext() {
            @Override
            public String getId() {
                return testConfiguration.getRemotingTestContext().getClientSession().getId();
            }

            @Override
            public ClientSessionExecutor createSessionExecutor() {
                return new ClientSessionExecutorImpl(new Executor() {
                    @Override
                    public void execute(Runnable command) {
                        testConfiguration.getRemotingTestContext().runLater(command);
                    }
                });
            }

            @Override
            public PropertyBinder getBinder() {
                return propertyBinder;
            }

            @Override
            public BeanManager getBeanManager() {
                return testConfiguration.getRemotingTestContext().getBeanManager();
            }

            @Override
            public RemotingEventBus getEventBus() {
                return eventBus;
            }

            @Override
            public ClientSession getClientSession() {
                return testConfiguration.getRemotingTestContext().getClientSession();
            }

            @Override
            public boolean isActive() {
                return true;
            }
        };
    }

    /**
     * Method to create a spring managed {@link RemotingEventBus} instance in singleton scope.
     *
     * @return the instance
     */
    @Bean(name = "remotingEventBus")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected RemotingEventBus createEventBus(final TestConfiguration testConfiguration) {
        Assert.requireNonNull(testConfiguration, "testConfiguration");

        final ServerRemotingContextProvider contextProvider = new ServerRemotingContextProvider() {
            @Override
            public ServerRemotingContext getContext(ClientSession clientSession) {
                return getCurrentContext();
            }

            @Override
            public ServerRemotingContext getContextById(String clientSessionId) {
                return getCurrentContext();
            }

            @Override
            public ServerRemotingContext getCurrentContext() {
                return testConfiguration.getRemotingTestContext();
            }
        };

        final DefaultRemotingEventBus eventBus = new DefaultRemotingEventBus();
        eventBus.init(contextProvider, new ClientSessionLifecycleHandlerImpl());
        return eventBus;
    }

    @Bean(name = "propertyBinder")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected PropertyBinder createPropertyBinder() {
        return new PropertyBinderImpl();
    }

    @Bean(name = "customScopeConfigurer")
    public static CustomScopeConfigurer createClientScope(final ClientSession clientSession) {
        Assert.requireNonNull(clientSession, "clientSession");
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope(ClientScopeImpl.CLIENT_SCOPE, new TestClientScope(clientSession));
        return configurer;
    }
}
