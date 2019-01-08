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
package dev.rico.internal.client;

import dev.rico.client.ClientConfiguration;
import dev.rico.client.Toolkit;
import dev.rico.client.concurrent.UiExecutor;
import dev.rico.client.spi.ServiceProvider;
import dev.rico.internal.client.config.ConfigurationFileLoader;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.ansi.PlatformLogo;
import dev.rico.internal.core.context.ContextManagerImpl;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.rico.internal.client.ClientConstants.UI_CONTEXT;
import static dev.rico.internal.core.RicoConstants.APPLICATION_CONTEXT;
import static dev.rico.internal.core.RicoConstants.APPLICATION_NAME_DEFAULT;
import static dev.rico.internal.core.RicoConstants.APPLICATION_NAME_PROPERTY;

public class ClientImpl {

    private static ClientImpl INSTANCE;

    private final Map<Class<?>, ServiceProvider> providers = new ConcurrentHashMap<>();

    private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    private final ClientConfiguration clientConfiguration;

    private AtomicBoolean isToolkitSet =  new AtomicBoolean(false);

    private Toolkit toolkit;

    private ClientImpl() {
        PlatformLogo.printLogo();
        this.clientConfiguration = ConfigurationFileLoader.loadConfiguration(ClientConstants.CONFIG_DEFAULT_LOCATION);
        Assert.requireNonNull(clientConfiguration, "clientConfiguration");

        ContextManagerImpl.getInstance().addGlobalContext(APPLICATION_CONTEXT, clientConfiguration.getProperty(APPLICATION_NAME_PROPERTY, APPLICATION_NAME_DEFAULT));

        final ServiceLoader<ServiceProvider> loader = ServiceLoader.load(ServiceProvider.class);
        final Iterator<ServiceProvider> iterator = loader.iterator();
        while (iterator.hasNext()) {
            ServiceProvider provider = iterator.next();
            if(provider.isActive(clientConfiguration)) {
                Class serviceClass = provider.getServiceType();
                Assert.requireNonNull(serviceClass, "serviceClass");
                if (providers.containsKey(serviceClass)) {
                    throw new RuntimeException("Can not register more than 1 implementation for service type " + serviceClass);
                }
                providers.put(serviceClass, provider);
            }
        }
        initImpl(new HeadlessToolkit());
    }

    public static UiExecutor getUiExecutor() {
        return getInstance().getUiExecutorImpl();
    }

    private UiExecutor getUiExecutorImpl() {
        return toolkit.getUiExecutor();
    }

    private void initImpl(final Toolkit toolkit) {
        this.toolkit = Assert.requireNonNull(toolkit, "toolkit");
        services.clear();
        isToolkitSet.set(true);
        ContextManagerImpl.getInstance().addGlobalContext(UI_CONTEXT, toolkit.getName());
    }

    private Set<Class<?>> implGetAllServiceTypes() {
        return Collections.unmodifiableSet(providers.keySet());
    }

    private <S> boolean hasServiceImpl(final Class<S> serviceClass) {
        Assert.requireNonNull(serviceClass, "serviceClass");
        if(!isToolkitSet.get()){
            throw new RuntimeException("Toolkit is not set!");
        }
        return providers.containsKey(serviceClass);
    }

    private synchronized <S> S getServiceImpl(final Class<S> serviceClass) {
        Assert.requireNonNull(serviceClass, "serviceClass");
        if(!isToolkitSet.get()){
            throw new RuntimeException("Toolkit is not set!");
        }
        if(services.containsKey(serviceClass)) {
            final S service = (S) services.get(serviceClass);
            return service;
        }
        final ServiceProvider<S> serviceProvider = providers.get(serviceClass);
        Assert.requireNonNull(serviceProvider, "serviceProvider");
        final S service = serviceProvider.getService(clientConfiguration);
        Assert.requireNonNull(service, "service");
        services.put(serviceClass, service);
        return service;
    }

    public static void init(Toolkit toolkit) {
        getInstance().initImpl(toolkit);
    }

    public static ClientConfiguration getClientConfiguration() {
        return getInstance().clientConfiguration;
    }

    public static <S> boolean hasService(final Class<S> serviceClass) {
        return getInstance().hasServiceImpl(serviceClass);
    }

    public static <S> S getService(final Class<S> serviceClass) {
        return getInstance().getServiceImpl(serviceClass);
    }

    public static Set<Class<?>> getAllServiceTypes() {
        return getInstance().implGetAllServiceTypes();
    }

    private static synchronized ClientImpl getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ClientImpl();
        }
        return INSTANCE;
    }
}
