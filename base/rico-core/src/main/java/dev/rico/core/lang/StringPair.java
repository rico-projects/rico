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
package dev.rico.core.lang;

import dev.rico.internal.core.lang.DefaultStringPair;

/**
 * Defines a key-value pair both of which are strings
 */
public interface StringPair extends Pair<String, String> {

    /**
     * Generates a new {@link StringPair} based on the given key and value.
     *
     * @param key the key
     * @param value the value
     * @return the pair
     */
    static StringPair of(String key, String value) {
        return new DefaultStringPair(key, value);
    }
}
