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
package dev.rico.internal.core.lang;

import dev.rico.core.lang.Tuple;

import java.util.Objects;

/**
 * Default implementation of {@link Tuple}
 * @param <K> type of the key
 * @param <V> type of the value
 */
public class DefaultTuple<K, V> implements Tuple<K, V> {

    private final K key;

    private final V value;

    public DefaultTuple(final K key, final V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DefaultTuple<?, ?> tuple = (DefaultTuple<?, ?>) o;
        return Objects.equals(key, tuple.key) &&
                Objects.equals(value, tuple.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
