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
package dev.rico.internal.server.bootstrap;

import dev.rico.internal.core.Assert;
import dev.rico.internal.core.ansi.PlatformLogo;
import dev.rico.internal.core.context.ContextManagerImpl;
import dev.rico.internal.server.config.ServerConfiguration;
import dev.rico.internal.server.mbean.MBeanRegistry;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static dev.rico.internal.core.RicoConstants.APPLICATION_NAME_DEFAULT;
import static dev.rico.internal.core.RicoConstants.APPLICATION_NAME_PROPERTY;
import static dev.rico.internal.core.context.ContextConstants.APPLICATION_NAME_CONTEXT;
import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.MBEAN_REGISTRATION;
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

        ContextManagerImpl.getInstance().setGlobalAttribute(APPLICATION_NAME_CONTEXT, configuration.getProperty(APPLICATION_NAME_PROPERTY, APPLICATION_NAME_DEFAULT));

        if (configuration.getBooleanProperty(PLATFORM_ACTIVE)) {
            PlatformLogo.printLogo();
            try {
                LOG.info("Will boot Rico now");

                servletContext.setAttribute(CONFIGURATION_ATTRIBUTE_NAME, configuration);
                configuration.log();

                MBeanRegistry.getInstance().setMbeanSupport(configuration.getBooleanProperty(MBEAN_REGISTRATION));

                final ManagedBeanFactory beanFactory = getBeanFactory(servletContext);
                final DefaultClasspathScanner classpathScanner = new DefaultClasspathScanner(configuration.getListProperty(ROOT_PACKAGE_FOR_CLASSPATH_SCAN));
                serverCoreComponents = new ServerCoreComponentsImpl(servletContext, configuration, classpathScanner, beanFactory);

                final Set<Class<?>> moduleClasses = classpathScanner.getTypesAnnotatedWith(ModuleDefinition.class);

                final Map<ModuleDefinition, ServerModule> modules = new TreeMap<>(Comparator.comparing(ModuleDefinition::order));

                for (final Class<?> moduleClass : moduleClasses) {
                    if (!ServerModule.class.isAssignableFrom(moduleClass)) {
                        throw new RuntimeException("Class " + moduleClass + " is annotated with " + ModuleDefinition.class.getSimpleName() + " but do not implement " + ServerModule.class.getSimpleName());
                    }
                    final ModuleDefinition moduleDefinition = moduleClass.getAnnotation(ModuleDefinition.class);
                    final String moduleName = moduleDefinition.name();

                    final boolean foundDuplicate = modules.keySet().stream()
                            .map(ModuleDefinition::name)
                            .anyMatch(m -> Objects.equals(m, moduleName));

                    if (foundDuplicate) {
                        throw new ModuleInitializationException("Module " + moduleName + " is defined multiple times");
                    }

                    final ServerModule instance = (ServerModule) moduleClass.getConstructor().newInstance();
                    if (instance.shouldBoot(serverCoreComponents.getConfiguration())) {
                        LOG.trace("Found Rico module {}", moduleName);
                        modules.put(moduleDefinition, instance);
                    } else {
                        LOG.trace("Skipping Rico module {}", moduleName);
                    }
                }

                LOG.info("Found {} active Rico modules", modules.size());

                for (final Map.Entry<ModuleDefinition, ServerModule> moduleEntry : modules.entrySet()) {
                    final ServerModule module = moduleEntry.getValue();
                    checkForNeededModules(modules, moduleEntry.getKey());
                    LOG.debug("Will initialize Rico module {}", moduleEntry.getKey());
                    module.initialize(serverCoreComponents);
                }
                LOG.info("Rico booted");
            } catch (Exception e) {
                throw new RuntimeException("Can not boot Rico", e);
            }
        } else {
            LOG.info("Rico is deactivated");
        }
    }

    private void checkForNeededModules(final Map<ModuleDefinition, ServerModule> modules, final ModuleDefinition definition) throws ModuleInitializationException {
        final Set<String> neededModules = Set.of(definition.moduleDependencies());
        final Set<String> foundModules = modules.keySet().stream()
                .filter(dependency -> neededModules.contains(dependency.name()))
                .filter(dependency -> definition.order() > dependency.order())
                .map(ModuleDefinition::name)
                .collect(Collectors.toSet());

        final Set<String> missingModules = new HashSet<>(neededModules);
        missingModules.removeAll(foundModules);

        if (!missingModules.isEmpty()) {
            throw new ModuleInitializationException("Module " + definition.name() + " depends on missing module(s) " + String.join(", ", missingModules));
        }

        // TODO: currently the exception does not state what exactly was the problem with a module. This could be improved.
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
