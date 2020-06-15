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

import dev.rico.core.Configuration;
import dev.rico.internal.core.Assert;
import dev.rico.server.spi.components.ManagedBeanFactory;
import dev.rico.server.spi.components.ClasspathScanner;
import dev.rico.server.spi.ServerCoreComponents;
import org.apiguardian.api.API;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ServerCoreComponentsImpl implements ServerCoreComponents {

    private final Map<Class<?>, Object> instances = new HashMap<>();

    protected ServerCoreComponentsImpl(final ServletContext servletContext, final Configuration configuration, final ThreadFactory threadFactory, final ClasspathScanner classpathScanner, final ManagedBeanFactory managedBeanFactory) {
        Assert.requireNonNull(servletContext, "servletContext");
        Assert.requireNonNull(configuration, "configuration");
        Assert.requireNonNull(threadFactory, "threadFactory");
        Assert.requireNonNull(classpathScanner, "classpathScanner");
        Assert.requireNonNull(managedBeanFactory, "managedBeanFactory");

        provideInstance(ServletContext.class, servletContext);
        provideInstance(Configuration.class, configuration);
        provideInstance(ThreadFactory.class, threadFactory);
        provideInstance(ClasspathScanner.class, classpathScanner);
        provideInstance(ManagedBeanFactory.class, managedBeanFactory);
    }

    public ServletContext getServletContext() {
        return getInstance(ServletContext.class);
    }

    public Configuration getConfiguration() {
        return getInstance(Configuration.class);
    }

    public <T> void provideInstance(final Class<T> cls, final T instance) {
        Assert.requireNonNull(cls, "cls");
        Assert.requireNonNull(instance, "instance");

        if(getInstance(cls) != null) {
            throw new IllegalStateException("Instance for class " + cls + " already provided");
        }
        instances.put(cls, instance);
    }

    public <T> T getInstance(final Class<T> cls) {
        Assert.requireNonNull(cls, "cls");
        return (T) instances.get(cls);
    }
}
