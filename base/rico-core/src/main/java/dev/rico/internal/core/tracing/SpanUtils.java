package dev.rico.internal.core.tracing;

import brave.Span;
import dev.rico.core.trace.SpanType;
import dev.rico.internal.core.Assert;

public class SpanUtils {

    public static Span.Kind convert(final SpanType spanType) {
        Assert.requireNonNull(spanType, "spanType");
        return Span.Kind.CLIENT;
    }
}
