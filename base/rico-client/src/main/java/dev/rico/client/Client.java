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
package dev.rico.client;

import dev.rico.internal.client.ClientConstants;
import dev.rico.internal.client.ClientImpl;
import dev.rico.internal.client.HeadlessToolkit;
import dev.rico.internal.client.config.ConfigurationFileLoader;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.ansi.PlatformLogo;
import dev.rico.internal.core.context.ContextManagerImpl;
import dev.rico.client.spi.ServiceProvider;
import org.apiguardian.api.API;

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
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(since = "0.19.0", status = EXPERIMENTAL)
public interface Client {

    static void init(Toolkit toolkit) {
        ClientImpl.init(toolkit);
    }

    static ClientConfiguration getClientConfiguration() {
        return ClientImpl.getClientConfiguration();
    }

    static <S> boolean hasService(final Class<S> serviceClass) {
        return ClientImpl.hasService(serviceClass);
    }

    static <S> S getService(final Class<S> serviceClass) {
        return ClientImpl.getService(serviceClass);
    }

    static Set<Class<?>> getAllServiceTypes() {
        return ClientImpl.getAllServiceTypes();
    }

}
