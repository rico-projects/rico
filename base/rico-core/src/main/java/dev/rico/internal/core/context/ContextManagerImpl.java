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
package dev.rico.internal.core.context;

import dev.rico.internal.core.Assert;
import dev.rico.internal.core.PlatformVersion;
import dev.rico.core.context.Context;
import dev.rico.core.context.ContextManager;
import dev.rico.core.functional.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static dev.rico.internal.core.RicoConstants.APPLICATION_CONTEXT;
import static dev.rico.internal.core.RicoConstants.CANONICAL_HOST_NAME_CONTEXT;
import static dev.rico.internal.core.RicoConstants.HOST_ADDRESS_CONTEXT;
import static dev.rico.internal.core.RicoConstants.HOST_NAME_CONTEXT;
import static dev.rico.internal.core.RicoConstants.PLATFORM_VERSION_CONTEXT;
import static dev.rico.internal.core.RicoConstants.UNNAMED_APPLICATION;

public class ContextManagerImpl implements ContextManager {

    private static final Logger LOG = LoggerFactory.getLogger(ContextManagerImpl.class);

    private static final ContextManagerImpl INSTANCE = new ContextManagerImpl();

    private final Set<Context> globalContexts;

    private final Lock globalContextsLock;

    private final ThreadLocal<Set<Context>> threadContexts;

    public ContextManagerImpl() {
        globalContexts = new HashSet<>();
        threadContexts = new ThreadLocal<>();
        globalContextsLock = new ReentrantLock();

        addGlobalContext(APPLICATION_CONTEXT, UNNAMED_APPLICATION);

        addGlobalContext(PLATFORM_VERSION_CONTEXT, PlatformVersion.getVersion());

        try {
            final InetAddress address = InetAddress.getLocalHost();
            addGlobalContext(HOST_NAME_CONTEXT, address.getHostName());
            addGlobalContext(CANONICAL_HOST_NAME_CONTEXT, address.getCanonicalHostName());
            addGlobalContext(HOST_ADDRESS_CONTEXT, address.getHostAddress());
        } catch (Exception e) {
            LOG.error("Can not define InetAddress for context!", e);
        }

    }

    @Override
    public Subscription addGlobalContext(final String type, final String value) {
        Assert.requireNonNull(type, "type");
        final Context context = new ContextImpl(type, value);
        globalContextsLock.lock();
        try {
            if(globalContexts.contains(context)) {
                globalContexts.remove(context);
            }
            globalContexts.add(context);
        } finally {
            globalContextsLock.unlock();
        }
        return () -> {
            globalContextsLock.lock();
            try {
                globalContexts.remove(context);
            } finally {
                globalContextsLock.unlock();
            }
        };
    }

    @Override
    public Subscription addThreadContext(final String type, String value) {
        Assert.requireNonNull(type, "type");
        final Set<Context> set = getOrCreateThreadContexts();
        final Context context = new ContextImpl(type, value);
        if(set.contains(context)) {
            set.remove(context);
        }
        set.add(context);
        return () -> set.remove(context);
    }

    @Override
    public Set<Context> getGlobalContexts() {
        globalContextsLock.lock();
        try {
            return Collections.unmodifiableSet(globalContexts);
        } finally {
            globalContextsLock.unlock();
        }
    }

    @Override
    public Set<Context> getThreadContexts() {
        return Collections.unmodifiableSet(getOrCreateThreadContexts());
    }

    private Set<Context> getOrCreateThreadContexts() {
        final Set<Context> set = Optional.ofNullable(threadContexts.get()).orElse(new HashSet<>());
        threadContexts.set(set);
        return set;
    }

    public static ContextManagerImpl getInstance() {
        return INSTANCE;
    }
}
