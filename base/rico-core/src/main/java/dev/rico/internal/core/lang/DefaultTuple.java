package dev.rico.internal.core.lang;

import dev.rico.core.lang.Tuple;

import java.util.Objects;

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
