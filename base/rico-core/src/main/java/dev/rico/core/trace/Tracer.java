package dev.rico.core.trace;

import java.util.function.Supplier;

public interface Tracer {

    boolean isInSpan();

    Span startSpan(String name, SpanType type);

    Span startChildSpan(Span parent, String name, SpanType type);

    Span getCurrentSpan();

    <T> T runInSpan(String name, Supplier<T> task);
}
