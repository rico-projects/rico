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
import org.apiguardian.api.API;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * A simple implementation of {@link ConfigurationProvider} that can be used as a base for a custom implementation. By default all methods return an empty map.
 *
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = EXPERIMENTAL)
public class ConfigurationProviderAdapter implements ConfigurationProvider {

    @Override
    public Map<String, String> getStringProperties() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, List<String>> getListProperties() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Boolean> getBooleanProperties() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Integer> getIntegerProperties() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Long> getLongProperties() {
        return Collections.emptyMap();
    }
}
