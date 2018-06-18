package dev.rico.tracing;

import java.util.function.Supplier;

public interface Tracer {

    boolean isInSpan();

    Span startSpan(String name, SpanType type);

    Span startChildSpan(Span parent, String name, SpanType type);

    Span getCurrentSpan();

    <T> T runInSpan(String name, Supplier<T> task);

    void runInSpan(String name, Runnable task);
}
