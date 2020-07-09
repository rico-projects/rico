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

import dev.rico.core.context.RicoApplicationContext;
import dev.rico.core.functional.Assignment;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.PlatformVersion;
import dev.rico.internal.core.os.OperationSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static dev.rico.internal.core.context.ContextConstants.APPLICATION_NAME_CONTEXT;
import static dev.rico.internal.core.context.ContextConstants.CANONICAL_HOST_NAME_CONTEXT;
import static dev.rico.internal.core.context.ContextConstants.HOST_ADDRESS_CONTEXT;
import static dev.rico.internal.core.context.ContextConstants.HOST_NAME_CONTEXT;
import static dev.rico.internal.core.context.ContextConstants.JAVA_VENDOR_CONTEXT;
import static dev.rico.internal.core.context.ContextConstants.JAVA_VENDOR_SYSTEM_PROPERTY;
import static dev.rico.internal.core.context.ContextConstants.JAVA_VERSION_CONTEXT;
import static dev.rico.internal.core.context.ContextConstants.JAVA_VERSION_SYSTEM_PROPERTY;
import static dev.rico.internal.core.context.ContextConstants.OS_CONTEXT;
import static dev.rico.internal.core.context.ContextConstants.PLATFORM_VERSION_CONTEXT;
import static dev.rico.internal.core.context.ContextConstants.UNNAMED_APPLICATION;

public class RicoApplicationContextImpl implements RicoApplicationContext {

    private static final Logger LOG = LoggerFactory.getLogger(RicoApplicationContextImpl.class);

    private static final RicoApplicationContextImpl INSTANCE = new RicoApplicationContextImpl();

    private final Map<String, String> globalContexts = new ConcurrentHashMap<>();

    private final ThreadLocal<Map<String, String>> threadContexts = ThreadLocal.withInitial(HashMap::new);

    RicoApplicationContextImpl() {
        setGlobalAttribute(APPLICATION_NAME_CONTEXT, UNNAMED_APPLICATION);
        setGlobalAttribute(PLATFORM_VERSION_CONTEXT, PlatformVersion.getVersion());

        setGlobalAttribute(OS_CONTEXT, OperationSystem.getLocalSystem().getShortName());

        setGlobalAttribute(JAVA_VERSION_CONTEXT, System.getProperty(JAVA_VERSION_SYSTEM_PROPERTY));
        setGlobalAttribute(JAVA_VENDOR_CONTEXT, System.getProperty(JAVA_VENDOR_SYSTEM_PROPERTY));

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
    public Assignment setGlobalAttribute(final String name, final String value) {
        Assert.requireNonNull(name, "name");
        Assert.requireNonNull(value, "value");

        globalContexts.put(name, value);
        return () -> globalContexts.remove(name);
    }

    @Override
    public Assignment setThreadLocalAttribute(final String name, String value) {
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

    public static RicoApplicationContextImpl getInstance() {
        return INSTANCE;
    }
}
