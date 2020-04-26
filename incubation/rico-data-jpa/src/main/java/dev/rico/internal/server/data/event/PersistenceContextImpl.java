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
package dev.rico.internal.server.data.event;

import dev.rico.core.functional.Subscription;
import dev.rico.internal.core.Assert;
import dev.rico.server.data.AbstractEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Listener for all JPA events. Defined as a singleton. {@link PersistenceListener} instances can be registered as listener.
 */
public class PersistenceContextImpl implements PersistanceContext {

    private static final PersistenceContextImpl instance = new PersistenceContextImpl();

    private final List<PersistenceListener> globalListeners;

    private final Map<Class, List<PersistenceListener>> listeners;

    private PersistenceContextImpl() {
        globalListeners = new CopyOnWriteArrayList<>();
        listeners = new ConcurrentHashMap<>();
    }

    /**
     * Returns the singleton
     * @return the singleton
     */
    public static final PersistenceContextImpl getInstance() {
        return instance;
    }

    /**
     * Adds a listener that will be notified for all JPA events
     * @param listener the listener
     */
    public final Subscription addListener(final PersistenceListener listener) {
        Assert.requireNonNull(listener, "listener");
        globalListeners.add(listener);
        return () -> globalListeners.remove(listener);
    }

    public final <T extends AbstractEntity> Subscription addListener(final Class<T> entityClass, final PersistenceListener listener) {
        Assert.requireNonNull(entityClass, "entityClass");
        Assert.requireNonNull(listener, "listener");
        listeners.computeIfAbsent(entityClass, e -> new CopyOnWriteArrayList<>()).add(listener);
        return () -> listeners.getOrDefault(entityClass, Collections.emptyList()).remove(listener);
    }

    public final void firePersisted(final AbstractEntity entity) {
        Assert.requireNonNull(entity, "entity");
        listeners.getOrDefault(entity.getClass(), Collections.emptyList()).forEach(l -> l.onEntityPersisted(entity));
        globalListeners.forEach(l -> l.onEntityPersisted(entity));
    }

    public final void fireRemoved(AbstractEntity entity) {
        Assert.requireNonNull(entity, "entity");
        listeners.getOrDefault(entity.getClass(), Collections.emptyList()).forEach(l -> l.onEntityRemoved(entity));
        globalListeners.forEach(l -> l.onEntityRemoved(entity));
    }

    public final void fireUpdated(AbstractEntity entity) {
        Assert.requireNonNull(entity, "entity");
        listeners.getOrDefault(entity.getClass(), Collections.emptyList()).forEach(l -> l.onEntityUpdated(entity));
        globalListeners.forEach(l -> l.onEntityUpdated(entity));
    }
}
