/*
 * Copyright 2018-2019 Karakun AG.
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
package dev.rico.internal.core.context;

import dev.rico.core.context.ContextManager;
import dev.rico.core.functional.Subscription;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.PlatformVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static dev.rico.internal.core.RicoConstants.APPLICATION_CONTEXT;
import static dev.rico.internal.core.RicoConstants.CANONICAL_HOST_NAME_CONTEXT;
import static dev.rico.internal.core.RicoConstants.HOST_ADDRESS_CONTEXT;
import static dev.rico.internal.core.RicoConstants.HOST_NAME_CONTEXT;
import static dev.rico.internal.core.RicoConstants.PLATFORM_VERSION_CONTEXT;
import static dev.rico.internal.core.RicoConstants.UNNAMED_APPLICATION;

public class ContextManagerImpl implements ContextManager {

    private static final Logger LOG = LoggerFactory.getLogger(ContextManagerImpl.class);

    private static final ContextManagerImpl INSTANCE = new ContextManagerImpl();

    private final Map<String, String> globalContexts = new ConcurrentHashMap<>();

    private final ThreadLocal<Map<String, String>> threadContexts = ThreadLocal.withInitial(HashMap::new);

    ContextManagerImpl() {
        setGlobalAttribute(APPLICATION_CONTEXT, UNNAMED_APPLICATION);
        setGlobalAttribute(PLATFORM_VERSION_CONTEXT, PlatformVersion.getVersion());

        try {
            final InetAddress address = InetAddress.getLocalHost();
            setGlobalAttribute(HOST_NAME_CONTEXT, address.getHostName());
            setGlobalAttribute(CANONICAL_HOST_NAME_CONTEXT, address.getCanonicalHostName());
            setGlobalAttribute(HOST_ADDRESS_CONTEXT, address.getHostAddress());
        } catch (Exception e) {
            LOG.error("Can not define InetAddress for context!", e);
        }
    }

    @Override
    public Subscription setGlobalAttribute(final String name, final String value) {
        Assert.requireNonNull(name, "name");
        Assert.requireNonNull(value, "value");

        globalContexts.put(name, value);
        return () -> globalContexts.remove(name);
    }

    @Override
    public Subscription setThreadLocalAttribute(final String name, String value) {
        Assert.requireNonNull(name, "name");
        Assert.requireNonNull(value, "value");

        final Map<String, String> map = threadContexts.get();
        map.put(name, value);
        return () -> map.remove(name);
    }

    @Override
    public Optional<String> getAttribute(String name) {
        Assert.requireNonNull(name, "name");

        final Map<String, String> map = threadContexts.get();
        if (map.containsKey(name)) {
            return Optional.of(map.get(name));
        }
        return Optional.ofNullable(globalContexts.get(name));
    }

    @Override
    public Map<String, String> getGlobalAttributes() {
        return Map.copyOf(globalContexts);
    }

    @Override
    public Map<String, String> getThreadLocalAttributes() {
        return Map.copyOf(threadContexts.get());
    }

    @Override
    public Map<String, String> getAttributes() {
        final HashMap<String, String> result = new HashMap<>(globalContexts);
        result.putAll(threadContexts.get());
        return Collections.unmodifiableMap(result);
    }

    public static ContextManagerImpl getInstance() {
        return INSTANCE;
    }
}
