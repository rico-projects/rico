package dev.rico.core.lang;

import dev.rico.internal.core.lang.DefaultTuple;

public interface Tuple<K, V> {

    K getKey();

    V getValue();

    static <A, B> Tuple<A, B> of(A a, B b) {
        return new DefaultTuple<>(a, b);
    }
}
