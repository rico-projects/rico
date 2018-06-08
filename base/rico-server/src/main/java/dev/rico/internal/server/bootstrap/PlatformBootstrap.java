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
package dev.rico.internal.server.bootstrap;

import dev.rico.core.concurrent.ExtendedThreadFactory;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.SimpleThreadFactory;
import dev.rico.internal.core.ansi.PlatformLogo;
import dev.rico.internal.core.context.ContextManagerImpl;
import dev.rico.internal.server.config.ServerConfiguration;
import dev.rico.internal.server.scanner.DefaultClasspathScanner;
import dev.rico.server.spi.ModuleDefinition;
import dev.rico.server.spi.ModuleInitializationException;
import dev.rico.server.spi.ServerCoreComponents;
import dev.rico.server.spi.ServerModule;
import dev.rico.server.spi.components.ManagedBeanFactory;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import static dev.rico.internal.core.RicoConstants.APPLICATION_CONTEXT;
import static dev.rico.internal.core.RicoConstants.APPLICATION_NAME_PROPERTY;
import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.PLATFORM_ACTIVE;
import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.ROOT_PACKAGE_FOR_CLASSPATH_SCAN;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class PlatformBootstrap {

    private static final Logger LOG = LoggerFactory.getLogger(PlatformBootstrap.class);

    private static final String CONFIGURATION_ATTRIBUTE_NAME = "ricoPlatformConfiguration";

    private static ServerCoreComponents serverCoreComponents;

    public void init(final ServletContext servletContext, final ServerConfiguration configuration) {
        Assert.requireNonNull(servletContext, "servletContext");
        Assert.requireNonNull(configuration, "configuration");

        ContextManagerImpl.getInstance().addGlobalContext(APPLICATION_CONTEXT, configuration.getProperty(APPLICATION_NAME_PROPERTY));

        if(configuration.getBooleanProperty(PLATFORM_ACTIVE)) {
            PlatformLogo.printLogo();
            try {
                LOG.info("Will boot Rico now");

                servletContext.setAttribute(CONFIGURATION_ATTRIBUTE_NAME, configuration);
                configuration.log();

                final ExtendedThreadFactory threadFactory = new SimpleThreadFactory();
                final ManagedBeanFactory beanFactory = getBeanFactory(servletContext);
                final DefaultClasspathScanner classpathScanner = new DefaultClasspathScanner(configuration.getListProperty(ROOT_PACKAGE_FOR_CLASSPATH_SCAN));
                serverCoreComponents = new ServerCoreComponentsImpl(servletContext, configuration, threadFactory, classpathScanner, beanFactory);

                final Set<Class<?>> moduleClasses = classpathScanner.getTypesAnnotatedWith(ModuleDefinition.class);

                final Map<String, ServerModule> modules = new HashMap<>();
                for (final Class<?> moduleClass : moduleClasses) {
                    if(!ServerModule.class.isAssignableFrom(moduleClass)) {
                        throw new RuntimeException("Class " + moduleClass + " is annoated with " + ModuleDefinition.class.getSimpleName() + " but do not implement " + ServerModule.class.getSimpleName());
                    }
                    ModuleDefinition moduleDefinition = moduleClass.getAnnotation(ModuleDefinition.class);
                    ServerModule instance = (ServerModule) moduleClass.newInstance();
                    modules.put(instance.getName(), instance);
                }

                LOG.info("Found {} Rico modules", modules.size());
                if (LOG.isTraceEnabled()) {
                    for (final String moduleName : modules.keySet()) {
                        LOG.trace("Found Rico module {}", moduleName);
                    }
                }

                for (final Map.Entry<String, ServerModule> moduleEntry : modules.entrySet()) {
                    LOG.debug("Will initialize Rico module {}", moduleEntry.getKey());
                    final ServerModule module = moduleEntry.getValue();
                    if (module.shouldBoot(serverCoreComponents.getConfiguration())) {
                        final List<String> neededModules = module.getModuleDependencies();
                        for (final String neededModule : neededModules) {
                            if (!modules.containsKey(neededModule)) {
                                throw new ModuleInitializationException("Module " + moduleEntry.getKey() + " depends on missing module " + neededModule);
                            }
                        }
                        module.initialize(serverCoreComponents);
                    }
                }
                LOG.info("Rico booted");
            } catch (Exception e) {
                throw new RuntimeException("Can not boot Rico", e);
            }
        } else {
            LOG.info("Rico is deactivated");
        }
    }


    private ManagedBeanFactory getBeanFactory(final ServletContext servletContext) {
        final ServiceLoader<ManagedBeanFactory> serviceLoader = ServiceLoader.load(ManagedBeanFactory.class);
        final Iterator<ManagedBeanFactory> serviceIterator = serviceLoader.iterator();
        if (serviceIterator.hasNext()) {
            final ManagedBeanFactory factory = serviceIterator.next();
            if (serviceIterator.hasNext()) {
                throw new IllegalStateException("More than 1 " + ManagedBeanFactory.class + " found!");
            }
            LOG.debug("Container Manager of type {} is used", factory.getClass().getSimpleName());
            factory.init(servletContext);
            return factory;
        } else {
            throw new IllegalStateException("No " + ManagedBeanFactory.class + " found!");
        }
    }

    public static ServerCoreComponents getServerCoreComponents() {
        return serverCoreComponents;
    }

}
