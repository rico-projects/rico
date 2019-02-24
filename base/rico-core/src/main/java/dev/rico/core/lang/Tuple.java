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
package dev.rico.core.lang;

import dev.rico.internal.core.lang.DefaultTuple;

/**
 * Defines a tuple that holds a key-value pair
 * @param <K> type of the key
 * @param <V> type of the value
 */
public interface Tuple<K, V> {

    /**
     * Returns the key
     * @return the key
     */
    K getKey();

    /**
     * Returns the value
     * @return the value
     */
    V getValue();

    /**
     * Generates a new {@link Tuple} based on the given key and value
     * @param key the key
     * @param value the value
     * @param <A> type of the key
     * @param <B> type of the value
     * @return the tuple
     */
    static <A, B> Tuple<A, B> of(A key, B value) {
        return new DefaultTuple<>(key, value);
    }
}
