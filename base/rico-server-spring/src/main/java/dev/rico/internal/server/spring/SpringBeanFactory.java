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
package dev.rico.internal.server.spring;

import dev.rico.core.concurrent.Scheduler;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.concurrent.SchedulerImpl;
import dev.rico.internal.core.context.RicoApplicationContextImpl;
import dev.rico.internal.server.bootstrap.PlatformBootstrap;
import dev.rico.internal.server.client.ClientSessionProvider;
import dev.rico.internal.server.servlet.ServerTimingFilter;
import dev.rico.core.context.RicoApplicationContext;
import dev.rico.server.client.ClientSession;
import dev.rico.server.spring.ClientScope;
import dev.rico.server.timing.ServerTiming;
import org.apiguardian.api.API;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.web.context.annotation.RequestScope;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Executor;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Provides all Beans and Scopes for CDI
 */
@Configuration
@API(since = "0.x", status = INTERNAL)
public class SpringBeanFactory {

    @Value("${rico.threadPool.coreSize:5}")
    private int threadPoolCoreSize;

    @Value("${rico.threadPool.maxSize:10}")
    private int threadPoolMaxSize;

    @Value("${rico.threadPool.waitOnShutdown:false}")
    private boolean threadPoolWaitForJobsOnShutdown;

    @Bean(name = "clientSession")
    @ClientScope
    protected ClientSession createClientSession() {
        final ClientSessionProvider provider = PlatformBootstrap.getServerCoreComponents().getInstance(ClientSessionProvider.class);
        Assert.requireNonNull(provider, "provider");
        return provider.getCurrentClientSession();
    }

    @Bean(name = "ricoApplicationContext")
    @ApplicationScope
    protected RicoApplicationContext createApplicationContext() {
        return RicoApplicationContextImpl.getInstance();
    }
    
    @Bean(name = "customScopeConfigurer")
    public static CustomScopeConfigurer createClientScope() {
        final CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope(ClientScopeImpl.CLIENT_SCOPE, new ClientScopeImpl());
        return configurer;
    }

    @Bean(name = "serverTiming")
    @RequestScope
    protected ServerTiming createServerTiming() {
        return (ServerTiming) Proxy.newProxyInstance(SpringBeanFactory.class.getClassLoader(), new Class[]{ServerTiming.class}, new InvocationHandler() {
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                return method.invoke(ServerTimingFilter.getCurrentTiming(), args);
            }
        });
    }

    @Bean(name = "scheduler")
    @ApplicationScope
    protected Scheduler createScheduler(@Qualifier("ricoThreadPool") final Executor executor) {
        Assert.requireNonNull(executor, "executor");
        return new SchedulerImpl(executor);
    }

    @Bean(name = "ricoThreadPool")
    @ApplicationScope
    protected SchedulingTaskExecutor createCachedThreadPool() {
        final ThreadPoolTaskExecutor result = new ThreadPoolTaskExecutor();
        result.setCorePoolSize(threadPoolCoreSize);
        result.setMaxPoolSize(threadPoolMaxSize);
        result.setWaitForTasksToCompleteOnShutdown(threadPoolWaitForJobsOnShutdown);
        return result;
    }
}
