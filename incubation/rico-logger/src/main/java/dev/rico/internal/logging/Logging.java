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

import dev.rico.internal.core.Assert;
import dev.rico.core.functional.Subscription;
import dev.rico.internal.logging.spi.LogMessage;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class Logging {

    private final static Logging INSTANCE = new Logging();

    private Logging() {
    }

    public void applyConfiguration(final LoggerConfiguration configuration) {
        throw new RuntimeException("Not yet implemented!");
    }

    public List<Marker> addGlobalMarker(final Marker marker) {
        throw new RuntimeException("Not yet implemented!");
    }

    public List<Marker> addGlobalMarker(final String... markers) {
        Assert.requireNonNull(markers, "markers");
        Arrays.asList(markers).forEach(m -> addGlobalMarker(m));
        return getGlobalMarkers();
    }

    public List<Marker> addGlobalMarker(final String marker) {
        return addGlobalMarker(getMarker(marker));
    }

    public List<Marker> removeGlobalMarker(final Marker marker) {
        throw new RuntimeException("Not yet implemented!");
    }

    public List<Marker> removeGlobalMarker(final String marker) {
        return removeGlobalMarker(getMarker(marker));
    }

    public List<Marker> addGlobalMarkers(final Collection<Marker> markers) {
        Assert.requireNonNull(markers, "markers");
        markers.forEach(m -> addGlobalMarker(m));
        return getGlobalMarkers();
    }

    public List<Marker> removeGlobalMarkers(final Collection<Marker> markers) {
        Assert.requireNonNull(markers, "markers");
        markers.forEach(m -> removeGlobalMarker(m));
        return getGlobalMarkers();
    }

    public List<Marker> getGlobalMarkers() {
        throw new RuntimeException("Not yet implemented!");
    }

    public List<LogMessage> getLogCache() {
        throw new RuntimeException("Not yet implemented!");
    }

    public void clearLogCache() {
        throw new RuntimeException("Not yet implemented!");
    }

    public void putInThreadContext(final String key, final String val) {
        throw new RuntimeException("Not yet implemented!");
    }

    public void removeFromThreadContext(final String key) {
        throw new RuntimeException("Not yet implemented!");
    }

    public void clearThreadContext() {
        throw new RuntimeException("Not yet implemented!");
    }

    public Marker getMarker(final String markerText) {
        return MarkerFactory.getMarker(markerText);
    }

    public Subscription addLoggingListener(final Consumer<List<LogMessage>> listener) {
        throw new RuntimeException("Not yet implemented!");
    }

    public static Logging getInstance() {
        return INSTANCE;
    }
}
