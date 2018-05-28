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
package dev.rico.internal.logging;

import dev.rico.internal.logging.spi.LogMessage;
import dev.rico.internal.logging.spi.LoggerBridge;
import dev.rico.internal.logging.spi.LoggerBridgeFactory;
import dev.rico.internal.core.Assert;
import dev.rico.core.functional.Subscription;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class RicoLoggerFactory implements ILoggerFactory {

    private final CopyOnWriteArrayList<String> markers = new CopyOnWriteArrayList<>();

    private static final List<LogMessage> messageCache = new CopyOnWriteArrayList<>();

    private static final List<Consumer<List<LogMessage>>> listeners = new CopyOnWriteArrayList<>();

    private static final int maxCacheSize = 10_000;

    private final AtomicBoolean configured = new AtomicBoolean(false);

    private final ConcurrentMap<String, RicoLogger> loggerMap = new ConcurrentHashMap();

    private final List<LoggerBridge> bridges = new CopyOnWriteArrayList<>();

    private LoggerConfiguration configuration;

    @Override
    public synchronized Logger getLogger(final String name) {
        if (!configured.get()) {
            configure(new LoggerConfiguration());
        }
        final RicoLogger logger = this.loggerMap.get(name);
        if (logger != null) {
            return logger;
        } else {
            final Level loggerLevel = configuration.getLevelFor(name);
            final RicoLogger newInstance = new RicoLogger(this, name, bridges, loggerLevel);
            final RicoLogger oldInstance = this.loggerMap.putIfAbsent(name, newInstance);
            return oldInstance == null ? newInstance : oldInstance;
        }
    }

    void reset() {
        this.loggerMap.clear();
    }

    public synchronized void configure(final LoggerConfiguration configuration) {
        Assert.requireNonNull(configuration, "configuration");
        bridges.clear();

        final Iterator<LoggerBridgeFactory> iterator = ServiceLoader.load(LoggerBridgeFactory.class).iterator();
        while (iterator.hasNext()) {
            final LoggerBridge bridge = iterator.next().create(configuration);
            if(bridge != null) {
                bridges.add(bridge);
            }
        }

        markers.clear();

        for(final RicoLogger logger : loggerMap.values()) {
            logger.updateBridges(Collections.unmodifiableList(bridges));
            final Level level = configuration.getLevelFor(logger.getName());
            logger.setLevel(level);
        }

        this.configuration = configuration;

        configured.set(true);
    }

    public synchronized List<String> addMarker(final String marker) {
        this.markers.add(Objects.requireNonNull(marker));
        return Collections.unmodifiableList(this.markers);
    }

    public synchronized List<String> removeMarker(final String marker) {
        this.markers.remove(Objects.requireNonNull(marker));
        return Collections.unmodifiableList(this.markers);
    }

    public synchronized List<String> addMarkers(final Collection<String> markers) {
        this.markers.addAll(Objects.requireNonNull(markers));
        return Collections.unmodifiableList(this.markers);
    }

    public synchronized List<String> removeMarkers(final Collection<String> markers) {
        this.markers.removeAll(Objects.requireNonNull(markers));
        return Collections.unmodifiableList(this.markers);
    }

    public List<String> getMarkers() {
        return Collections.unmodifiableList(markers);
    }

    public void addToCache(final LogMessage logMessage) {
        //TODO: Must be done in background thread...
        while(messageCache.size() > maxCacheSize) {
            messageCache.remove(0);
        }
        messageCache.add(logMessage);

        listeners.forEach(l -> {
            try {
                l.accept(messageCache);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void applyConfiguration(final LoggerConfiguration configuration) {
        final ILoggerFactory factory = LoggerFactory.getILoggerFactory();
        Objects.requireNonNull(factory);
        if(factory instanceof RicoLoggerFactory) {
            ((RicoLoggerFactory) factory).configure(configuration);
        } else {
            throw new IllegalStateException(LoggerFactory.class + " is not of type " + RicoLoggerFactory.class);
        }
    }

    public static List<LogMessage> getLogCache() {
        return messageCache;
    }

    public static void clearCache() {
        messageCache.clear();
        listeners.forEach(l -> {
            try {
                l.accept(messageCache);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static Subscription addListener(final Consumer<List<LogMessage>> listener) {
        Assert.requireNonNull(listener, "listener");
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }
}
