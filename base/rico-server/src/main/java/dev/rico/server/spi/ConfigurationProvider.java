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
package dev.rico.server.spi;

import dev.rico.core.Configuration;
import org.apiguardian.api.API;

import java.util.List;
import java.util.Map;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * A provider for configuration properties. This can be used to define the default values for all the properties of a module.
 *
 * @author Hendrik Ebbers
 *
 * @see Configuration
 */
@API(since = "0.x", status = EXPERIMENTAL)
public interface ConfigurationProvider {

    /**
     * Returns a map of property value pairs. All values are string based.
     * @return a map of property value pairs
     */
    Map<String, String> getStringProperties();

    /**
     * Returns a map of property value pairs. All values are a list of string values.
     * @return a map of property value pairs
     */
    Map<String, List<String>> getListProperties();

    /**
     * Returns a map of property value pairs. All values are boolean based.
     * @return a map of property value pairs
     */
    Map<String, Boolean> getBooleanProperties();

    /**
     * Returns a map of property value pairs. All values are int based.
     * @return a map of property value pairs
     */
    Map<String, Integer> getIntegerProperties();

    /**
     * Returns a map of property value pairs. All values are long based.
     * @return a map of property value pairs
     */
    Map<String, Long> getLongProperties();

}
