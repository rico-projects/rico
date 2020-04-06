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

import dev.rico.server.spi.ConfigurationProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SimpleConfigurationProvider implements ConfigurationProvider {

    private final Map<String, String> stringProperties = new HashMap<>();

    private final Map<String, List<String>> listProperties = new HashMap<>();

    private final Map<String, Boolean> booleanProperties = new HashMap<>();

    private final Map<String, Integer> intProperties = new HashMap<>();

    private final Map<String, Long> longProperties = new HashMap<>();

    protected final void addString(final String key, final String value) {
        stringProperties.put(key, value);
    }

    protected final void addList(final String key, final List<String> value) {
        listProperties.put(key, value);
    }

    protected final void addBoolean(final String key, final boolean value) {
        booleanProperties.put(key, value);
    }

    protected final void addInt(final String key, final int value) {
        intProperties.put(key, value);
    }

    protected final void addLong(final String key, final long value) {
        longProperties.put(key, value);
    }

    @Override
    public final Map<String, String> getStringProperties() {
        return Collections.unmodifiableMap(stringProperties);
    }

    @Override
    public final Map<String, List<String>> getListProperties() {
        return Collections.unmodifiableMap(listProperties);
    }

    @Override
    public final Map<String, Boolean> getBooleanProperties() {
        return Collections.unmodifiableMap(booleanProperties);
    }

    @Override
    public final Map<String, Integer> getIntegerProperties() {
        return Collections.unmodifiableMap(intProperties);
    }

    @Override
    public final Map<String, Long> getLongProperties() {
        return Collections.unmodifiableMap(longProperties);
    }
}
